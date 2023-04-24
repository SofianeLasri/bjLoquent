/*
 * The MIT License
 *
 * Copyright 2018 Derick Felix.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.bjloquent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author derickfelix
 * @date Feb 24, 2018
 */
public abstract class Model {
    protected String primaryKeyName = "id";
    protected String tableName;

    public Model() {
        tableName = Utility.tableOf(this);
    }

    /**
     * @return the primaryKeyName
     */
    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Creates a new entity into a table with the same name of a model child but
     * in plural, e.g. a model <code>class Person extends Model</code> will have
     * all of its fields persisted into a table called <code>persons</code>.
     * The <code>id</code> field will be set to the last inserted id if it not null.
     */
    public void create() {
        Method[] methods = this.getClass().getDeclaredMethods();
        List<Field> fields = Utility.getFields(methods, this, false, primaryKeyName);
        Connector connector = Connector.getInstance();

        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
        Field primaryKey = null;

        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            if (field.isPrimaryKey()) {
                primaryKey = field;
            }

            sql.append(field.getName());

            if ((i + 1) != fields.size()) {
                sql.append(", ");
            }
        }

        if (primaryKey == null) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "id cannot be null");
            return;
        }

        sql.append(") VALUES (");
        for (int i = 0; i < fields.size(); i++) {
            sql.append("?");
            if ((i + 1) != fields.size()) {
                sql.append(", ");
            }
        }

        sql.append(")");

        try {
            PreparedStatement statement = connector.open().prepareStatement(
                    String.valueOf(sql),
                    PreparedStatement.RETURN_GENERATED_KEYS
            );

            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                statement.setObject(i + 1, field.getValue());
            }

            statement.executeUpdate();
            if (primaryKey.getType().equals("int")) {
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    String setPrimaryKeyMethod = "set" + primaryKey.getName().substring(0, 1).toUpperCase()
                            + primaryKey.getName().substring(1);
                    this.getClass().getDeclaredMethod(
                            setPrimaryKeyMethod, int.class).invoke(this, rs.getInt(1)
                    );
                }
            }
        } catch (SQLException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            Logger.getLogger(Model.class.getName()).log(
                    Level.SEVERE,
                    "Error while creating a new entity in the database",
                    e
            );
        }
    }

    /**
     * Updates an entity in a table with the same name of a model child but in
     * plural, e.g. a model <code>class Person extends Model</code> will have
     * all of its fields updated, in a table called <code>persons</code>.
     */
    public void save() {
        Method[] methods = this.getClass().getDeclaredMethods();
        List<Field> fields = Utility.getFields(methods, this, false, primaryKeyName);
        Connector connector = Connector.getInstance();

        Field primaryKey = null;
        StringBuilder preparedSql = new StringBuilder("UPDATE " + tableName + " SET ");
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);

            if (!field.isPrimaryKey()) {
                preparedSql.append(field.getName()).append(" = ?");

                if ((i + 1) < fields.size()) {
                    preparedSql.append(", ");
                }
            } else {
                primaryKey = field;
            }
        }

        if (primaryKey == null) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "id cannot be null");
            return;
        }

        preparedSql.append(" WHERE ").append(primaryKeyName).append(" = ?");

        String sql = preparedSql.toString();

        try {
            PreparedStatement statement = connector.open().prepareStatement(sql);

            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                if (!field.isPrimaryKey()) {
                    statement.setObject(i, field.getValue());
                }
            }

            statement.setObject(fields.size(), primaryKey.getValue());
            statement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Deletes an entity in the model table.
     */
    public void delete() {
        String sql = "DELETE FROM " + tableName;
        Connector connector = Connector.getInstance();

        Method[] methods = this.getClass().getDeclaredMethods();
        List<Field> fields = Utility.getFields(methods, this, false, primaryKeyName);
        Field primaryKey = null;
        for (Field field : fields) {
            if (field.isPrimaryKey()) {
                primaryKey = field;
                break;
            }
        }

        if (primaryKey == null) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "id cannot be null");
            return;
        }

        sql += " WHERE " + primaryKeyName + " = ?";
        try {
            PreparedStatement statement = connector.open().prepareStatement(sql);
            statement.setObject(1, primaryKey.getValue());
            statement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(
                    Level.SEVERE,
                    "Could not delete entity with id " + primaryKey.getValue() + " from table " + tableName,
                    ex
            );
        }
    }

    /**
     * Finds an entity in the model table
     *
     * @param targetClass     The class of the model
     * @param primaryKeyValue The value of the primary key
     * @param <SubModel>      The model class
     * @return The model instance
     */
    public static <SubModel extends Model> SubModel find(Class<SubModel> targetClass, Object primaryKeyValue) {
        SubModel targetModel = null;
        try {
            targetModel = targetClass.getDeclaredConstructor().newInstance();
            String tableName = targetModel.getTableName();
            String primaryKeyName = targetModel.getPrimaryKeyName();

            String sql = "SELECT * FROM " + tableName + " WHERE " + primaryKeyName + " = ?";
            Connector connector = Connector.getInstance();

            PreparedStatement statement = connector.open().prepareStatement(sql);
            statement.setObject(1, primaryKeyValue);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                targetModel.setModelFields(rs);
            }
        } catch (SQLException | NoSuchMethodException | SecurityException | IllegalAccessException |
                 IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(targetClass.getName()).log(
                    Level.SEVERE,
                    "Could not find entity with id " + primaryKeyValue,
                    ex
            );
        } catch (InstantiationException e) {
            Logger.getLogger(targetClass.getName()).log(
                    Level.SEVERE,
                    "Could not instantiate model",
                    e
            );
        }

        return targetModel;
    }

    /**
     * Finds all entities in the model table that match the given conditions.
     *
     * @param targetClass The class of the model
     * @param columns     The columns to check
     * @param operators   The operators to use (e.g. =, >, <, etc.)
     * @param values      The values to check against
     * @param <SubModel>  The model class
     * @return A list of models that match the conditions
     */
    public static <SubModel extends Model> List<SubModel> where(
            Class<SubModel> targetClass,
            String[] columns,
            String[] operators,
            Object[] values
    ) {
        Connector connector = Connector.getInstance();
        List<SubModel> models = new ArrayList<>();

        if (columns.length != operators.length || columns.length != values.length) {
            Logger.getLogger(targetClass.getName()).log(
                    Level.SEVERE,
                    "Columns, operators and values must have the same length"
            );
            return models;
        }

        try {
            SubModel targetModel = targetClass.getDeclaredConstructor().newInstance();
            String tableName = targetModel.getTableName();
            String primaryKeyName = targetModel.getPrimaryKeyName();

            StringBuilder sql = new StringBuilder("SELECT * FROM " + tableName + " WHERE ");
            for (int i = 0; i < columns.length; i++) {
                sql.append(columns[i]).append(" ").append(operators[i]).append(" ?");

                if ((i + 1) < columns.length) {
                    sql.append(" AND ");
                }
            }

            PreparedStatement statement = connector.open().prepareStatement(sql.toString());
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                SubModel model = targetClass.getDeclaredConstructor().newInstance();
                model.setModelFields(rs);
                models.add(model);
            }
        } catch (SQLException | NoSuchMethodException | SecurityException | IllegalAccessException |
                 InvocationTargetException e) {
            Logger.getLogger(targetClass.getName()).log(
                    Level.SEVERE,
                    "Error while executing where query.",
                    e
            );
        } catch (InstantiationException e) {
            Logger.getLogger(targetClass.getName()).log(
                    Level.SEVERE,
                    "Could not instantiate model",
                    e
            );
        }

        return models;
    }

    /**
     * Helper method for where() that only takes one condition with equals operator
     *
     * @param targetClass The class of the model
     * @param column      The column to check
     * @param value       The value to check against
     * @param <SubModel>  The model class
     * @return A list of models that match the conditions
     */
    public static <SubModel extends Model> List<SubModel> where(
            Class<SubModel> targetClass,
            String column,
            Object value
    ) {
        return where(targetClass, new String[]{column}, new String[]{"="}, new Object[]{value});
    }

    /**
     * Helper method for where() that only takes one condition
     *
     * @param targetClass The class of the model
     * @param column      The column to check
     * @param operator    The operator to use (e.g. =, >, <, etc.)
     * @param value       The value to check against
     * @param <SubModel>  The model class
     * @return A list of models that match the conditions
     */
    public static <SubModel extends Model> List<SubModel> where(
            Class<SubModel> targetClass,
            String column,
            String operator,
            Object value
    ) {
        return where(targetClass, new String[]{column}, new String[]{operator}, new Object[]{value});
    }

    /**
     * Sets the model fields from a result set
     * @param rs The result set
     */
    public void setModelFields(ResultSet rs) {
        Method[] methods = this.getClass().getDeclaredMethods();
        List<Field> fields = Utility.getFields(methods, this, true, primaryKeyName);

        for (Field field : fields) {
            String setMethod = "set" + field.getName().substring(0, 1).toUpperCase()
                    + field.getName().substring(1);

            // We retrieve the object from the result set
            Object resultSetObject;
            try {
                resultSetObject = rs.getObject(field.getName());
            } catch (SQLException e) {
                Logger.getLogger(getClass().getName()).log(
                        Level.SEVERE,
                        "Could not get object " + field.getName() + " from result set",
                        e
                );
                continue;
            }

            try {
                // We invoke the setter method with the object from the result set
                this.getClass().getDeclaredMethod(
                        setMethod,
                        field.getTypeClass()
                ).invoke(
                        this,
                        resultSetObject
                );
            } catch (NoSuchMethodException e) {
                Logger.getLogger(getClass().getName()).log(
                        Level.SEVERE,
                        "Could not find method " + setMethod + " in class " + getClass().getName(),
                        e
                );
            } catch (IllegalAccessException e) {
                Logger.getLogger(getClass().getName()).log(
                        Level.SEVERE,
                        "Could not access method " + setMethod + " in class " + getClass().getName(),
                        e
                );
            } catch (IllegalArgumentException e) {
                Logger.getLogger(getClass().getName()).log(
                        Level.SEVERE,
                        "Illegal argument for method " + setMethod + " in class " + getClass().getName() +
                                ". Expected " + field.getTypeClass().getName() + " but got " +
                                resultSetObject.getClass().getName(),
                        e
                );
            } catch (InvocationTargetException e) {
                Logger.getLogger(getClass().getName()).log(
                        Level.SEVERE,
                        "Could not invoke method " + setMethod + " in class " + getClass().getName(),
                        e
                );
            }
        }
    }
}
