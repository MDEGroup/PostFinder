/**
 * Copyright (c) 2010-2017 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evolveum.polygon.connector.jdbc.example;


import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author Lukas Skublik
 *
 */

import java.util.Set;

import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.exceptions.ConnectorIOException;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ObjectClassInfoBuilder;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.OperationalAttributeInfos;
import org.identityconnectors.framework.common.objects.OperationalAttributes;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.SchemaBuilder;
import org.identityconnectors.framework.common.objects.SyncDeltaBuilder;
import org.identityconnectors.framework.common.objects.SyncDeltaType;
import org.identityconnectors.framework.common.objects.SyncResultsHandler;
import org.identityconnectors.framework.common.objects.SyncToken;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.AndFilter;
import org.identityconnectors.framework.common.objects.filter.CompositeFilter;
import org.identityconnectors.framework.common.objects.filter.ContainsFilter;
import org.identityconnectors.framework.common.objects.filter.EndsWithFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.Filter;
import org.identityconnectors.framework.common.objects.filter.FilterBuilder;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.common.objects.filter.NotFilter;
import org.identityconnectors.framework.common.objects.filter.OrFilter;
import org.identityconnectors.framework.common.objects.filter.StartsWithFilter;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.CreateOp;
import org.identityconnectors.framework.spi.operations.SchemaOp;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.identityconnectors.framework.spi.operations.SyncOp;
import org.identityconnectors.framework.spi.operations.TestOp;
import org.identityconnectors.framework.spi.operations.UpdateOp;

import com.evolveum.polygon.connector.jdbc.AbstractJdbcConnector;
import com.evolveum.polygon.connector.jdbc.DeleteBuilder;
import com.evolveum.polygon.connector.jdbc.SQLRequest;
import com.evolveum.polygon.connector.jdbc.SelectSQLBuilder;
import com.evolveum.polygon.connector.jdbc.InsertSQLBuilder;
import com.evolveum.polygon.connector.jdbc.JdbcUtil;
import com.evolveum.polygon.connector.jdbc.InsertOrUpdateSQL;
import com.evolveum.polygon.connector.jdbc.SQLParameterBuilder;
import com.evolveum.polygon.connector.jdbc.UpdateSQLBuilder;

import org.identityconnectors.framework.spi.operations.DeleteOp;

@ConnectorClass(displayNameKey = "connector.gitlab.rest.display", configurationClass = ExampleJdbcConfiguration.class)
public class ExampleJdbcConnector extends AbstractJdbcConnector<ExampleJdbcConfiguration> implements TestOp, SchemaOp, CreateOp, DeleteOp, UpdateOp, 
		SearchOp<Filter>, SyncOp {

	private static final Log LOGGER = Log.getLog(ExampleJdbcConnector.class);

	private final static String TABLE_OF_ACCOUNTS = "TestUsers";
	private final static String KEY_OF_ACCOUNTS_TABLE = "ID";
	private final static String COLUMN_WITH_PASSWORD = "PASSWORD";
	private final static String COLUMN_WITH_NAME = "USERNAME";
	private final static String COLUMN_WITH_LAST_MODIFICATION = "LASTMODIFICATION";
	private final static String LIKE = " LIKE ";
	private final static String NOT_LIKE = " NOT LIKE ";
	private final static String AND = " AND ";
	private final static String OR = " OR ";
	
	@Override
	public FilterTranslator<Filter> createFilterTranslator(ObjectClass objectClass, OperationOptions option) {
		return new FilterTranslator<Filter>() {
			@Override
			public List<Filter> translate(Filter filter) {
				return CollectionUtil.newList(filter);
			}
		};
	}

	@Override
	public void executeQuery(ObjectClass objectClass, Filter query, ResultsHandler handler, OperationOptions options) {
		if (objectClass == null) {
			LOGGER.error("Attribute of type ObjectClass not provided.");
			throw new InvalidAttributeValueException("Attribute of type ObjectClass is not provided.");
		}

		if (handler == null) {
			LOGGER.error("Attribute of type ResultsHandler not provided.");
			throw new InvalidAttributeValueException("Attribute of type ResultsHandler is not provided.");
		}
		
		if (options == null) {
			LOGGER.error("Attribute of type OperationOptions not provided.");
			throw new InvalidAttributeValueException("Attribute of type OperationOptions is not provided.");
		}
		LOGGER.info("executeQuery on {0}, filter: {1}, options: {2}", objectClass, query, options);
		if (objectClass.is(ObjectClass.ACCOUNT_NAME)) {
			SelectSQLBuilder selectSqlB = new SelectSQLBuilder();
			String whereClause = cretedWhereClauseFromFilter(query);
			String[] names = options.getAttributesToGet();
		
			if(names != null){
				String[] quotedName = new String[names.length];;
				for(int i =0; i < names.length; i++){
					LOGGER.info("name of requested column {0}", quoteName(getConfiguration().getQuoting(), names[i]));
					if(names[i].equals(Uid.NAME)){
						quotedName[i] = quoteName(getConfiguration().getQuoting(), KEY_OF_ACCOUNTS_TABLE.toLowerCase());
					} else if(names[i].equals(Name.NAME)){
						quotedName[i] = quoteName(getConfiguration().getQuoting(), COLUMN_WITH_NAME.toLowerCase());
					} else if(names[i].equals(OperationalAttributes.PASSWORD_NAME)){
						quotedName[i] = quoteName(getConfiguration().getQuoting(), COLUMN_WITH_PASSWORD.toLowerCase());
					} else {
						quotedName[i] = quoteName(getConfiguration().getQuoting(), names[i]);
					}
				}
				selectSqlB.setAllNamesOfColumns(quotedName);
			}
			selectSqlB.addNameOfTable(TABLE_OF_ACCOUNTS);
			selectSqlB.setWhereClause(whereClause);
			String sqlRequest = selectSqlB.build();
		
			List<List<Attribute>> rowsOfTable = executeQueryOnTable(sqlRequest);
		
		
			convertListOfRowsToConnectorObject(rowsOfTable, handler);
		}  else {
			LOGGER.error("Attribute of type ObjectClass is not supported.");
			throw new UnsupportedOperationException("Attribute of type ObjectClass is not supported.");
		}

	}
	
	
	private void convertListOfRowsToConnectorObject(List<List<Attribute>> rowsOfTable, SyncResultsHandler handler){
		
		
		for (List<Attribute> row : rowsOfTable){
			Attribute token = AttributeUtil.find(COLUMN_WITH_LAST_MODIFICATION, new HashSet<Attribute>(row));
			ConnectorObject ret = convertRowToConnectorObject(row);
			if(ret != null && token != null && token.getValue() != null && token.getValue().get(0) != null){
				SyncDeltaBuilder synDeltaB = new SyncDeltaBuilder();
				synDeltaB.setToken(new SyncToken(token.getValue().get(0)));
				synDeltaB.setObject(ret);
				synDeltaB.setDeltaType(SyncDeltaType.CREATE_OR_UPDATE);
				handler.handle(synDeltaB.build());
			}
		}
	}
	
	private void convertListOfRowsToConnectorObject(List<List<Attribute>> rowsOfTable, ResultsHandler handler){
		
		for (List<Attribute> row : rowsOfTable){
			ConnectorObject ret = convertRowToConnectorObject(row);
			if(ret != null){
				handler.handle(ret);
			}
		}
	}
	
	private ConnectorObject convertRowToConnectorObject(List<Attribute> rowOfTable){
		
		ConnectorObject ret = null;
		ConnectorObjectBuilder connObjB = new ConnectorObjectBuilder();
		connObjB.setObjectClass(ObjectClass.ACCOUNT);
		for(Attribute attr : rowOfTable){
			if(attr.getName().equalsIgnoreCase(KEY_OF_ACCOUNTS_TABLE)){
				connObjB.addAttribute(new Uid(String.valueOf(attr.getValue().get(0))));
			} else if(attr.getName().equalsIgnoreCase(COLUMN_WITH_NAME)){
				connObjB.addAttribute(new Name(String.valueOf(attr.getValue().get(0))));
			} else if(attr.getName().equalsIgnoreCase(COLUMN_WITH_PASSWORD)){
				if(!getConfiguration().getSuppressPassword()){
					connObjB.addAttribute(AttributeBuilder.build(OperationalAttributes.PASSWORD_NAME,new GuardedString(String.valueOf(attr.getValue().get(0)).toCharArray())));
				}
			} else {
				connObjB.addAttribute(AttributeBuilder.build(attr.getName(),attr.getValue()));
			}
		}
		ret = connObjB.build();
		return ret;
	}
	
	@Override
	public void delete(ObjectClass objectClass, Uid uid, OperationOptions option) {
		if (objectClass == null) {
			LOGGER.error("Attribute of type ObjectClass not provided.");
			throw new InvalidAttributeValueException("Attribute of type ObjectClass not provided.");
		}
		if (option == null) {
			LOGGER.error("Attribute of type OperationOptions not provided.");
			throw new InvalidAttributeValueException("Attribute of type OperationOptions not provided.");
		}
		
		if (uid == null) {
			LOGGER.error("Attribute of type uid not provided.");
			throw new InvalidAttributeValueException("Attribute of type uid not provided.");
		}
		
		if (objectClass.is(ObjectClass.ACCOUNT_NAME)) { // __ACCOUNT__
			
			DeleteBuilder deleteBuilder = new DeleteBuilder();
			executeUpdateOnTable(deleteBuilder.build(TABLE_OF_ACCOUNTS, uid, quoteName(getConfiguration().getQuoting(), KEY_OF_ACCOUNTS_TABLE)).toLowerCase());
			
		}  else {
			LOGGER.error("Attribute of type ObjectClass is not supported.");
			throw new UnsupportedOperationException("Attribute of type ObjectClass is not supported.");
		}
		
	}

	@Override
	public Uid create(ObjectClass objectClass, Set<Attribute> attrs, OperationOptions option) {
		if (objectClass == null) {
			LOGGER.error("Attribute of type ObjectClass not provided.");
			throw new InvalidAttributeValueException("Attribute of type ObjectClass not provided.");
		}
		if (attrs == null) {
			LOGGER.error("Attribute of type Set<Attribute> not provided.");
			throw new InvalidAttributeValueException("Attribute of type Set<Attribute> not provided.");
		}
		if (option == null) {
			LOGGER.error("Attribute of type OperationOptions not provided.");
			throw new InvalidAttributeValueException("Attribute of type OperationOptions not provided.");
		}
		
		Set<Attribute> attributes = new HashSet<Attribute>();
		attributes.addAll(attrs);
		if (objectClass.is(ObjectClass.ACCOUNT_NAME)) { // __ACCOUNT__
			
			List<String> excludedNames = new ArrayList<String>();
			excludedNames.add(COLUMN_WITH_PASSWORD);
			excludedNames.add(COLUMN_WITH_NAME);
			buildAttributeInfosFromTable(TABLE_OF_ACCOUNTS, quoteName(getConfiguration().getQuoting(), KEY_OF_ACCOUNTS_TABLE), excludedNames);
			List<String> namesOfRequiredColumns = getNamesOfRequiredColumns();
			Name name = AttributeUtil.getNameFromAttributes(attributes);
			
			if(getConfiguration().isEnableEmptyStr()) {
				LOGGER.info("Requested attributes which name will be not find in input attribute set  should be empty.");
				for(String nameOfReqColumn : namesOfRequiredColumns) {
					if(AttributeUtil.find(nameOfReqColumn, attributes) == null){
						attributes.add(AttributeBuilder.build(nameOfReqColumn, ""));
					}
				}
				if(name == null){
					attributes.add(new Name(""));
				}
			} else {
				
				if(name == null){
					throw new InvalidAttributeValueException("Input attributes do not contains Name attribute.");
				}
				
				for(String nameOfReqColumn : namesOfRequiredColumns) {
					if(AttributeUtil.find(nameOfReqColumn, attributes) == null){
						StringBuilder sb = new StringBuilder();
						sb.append("Input attributes do not contains required attribute ").append(nameOfReqColumn).append(".");
						throw new InvalidAttributeValueException(sb.toString());
					}
				}
			}
			
			GuardedString password = AttributeUtil.getPasswordValue(attributes);
			if(password == null){
				throw new InvalidAttributeValueException("Input attributes do not contains password attribute.");
			}
			
			
			SQLRequest insertSQL = buildInsertOrUpdateSql(attributes, true, null);
			executeUpdateOnTable(insertSQL.getSql(), insertSQL.getParameters());
			return returnUidFromName(name.getNameValue());
			
		}  else {
			LOGGER.error("Attribute of type ObjectClass is not supported.");
			throw new UnsupportedOperationException("Attribute of type ObjectClass is not supported.");
		}
	}
	

	@Override
	public Schema schema() {
		
		SchemaBuilder schemaBuilder = new SchemaBuilder(ExampleJdbcConnector.class);
		List<String> excludedNames = new ArrayList<String>();
		excludedNames.add(COLUMN_WITH_PASSWORD);
		Set<AttributeInfo> attributesFromUsersTable = buildAttributeInfosFromTable(TABLE_OF_ACCOUNTS, quoteName(getConfiguration().getQuoting(), KEY_OF_ACCOUNTS_TABLE), excludedNames);
		ObjectClassInfoBuilder accountObjClassBuilder = new ObjectClassInfoBuilder();
		
		accountObjClassBuilder.addAllAttributeInfo(attributesFromUsersTable);
		
		accountObjClassBuilder.addAttributeInfo(OperationalAttributeInfos.PASSWORD);
		
		schemaBuilder.defineObjectClass(accountObjClassBuilder.build());
		return schemaBuilder.build();
	}
	
	private SQLRequest buildInsertOrUpdateSql(Set<Attribute> attributes, Boolean insert, Uid uid){
		
		InsertOrUpdateSQL requestBuilder;
		
		if(insert){
			requestBuilder = new InsertSQLBuilder();
		}else {
			requestBuilder = new UpdateSQLBuilder();
			((UpdateSQLBuilder)requestBuilder).setWhereClause(uid, KEY_OF_ACCOUNTS_TABLE.toLowerCase());
		}
		
		requestBuilder.setNameOfTable(quoteName(getConfiguration().getQuoting(), TABLE_OF_ACCOUNTS));
		for(Attribute attr : attributes){
			if(attr.getName().equalsIgnoreCase(OperationalAttributes.PASSWORD_NAME)){
				final StringBuilder sbPass = new StringBuilder();
				((GuardedString)attr.getValue().get(0)).access(new GuardedString.Accessor() {
					@Override
					public void access(char[] chars) {
						sbPass.append(new String(chars));
					}
				});
				requestBuilder.setNameAndValueOfColumn(new SQLParameterBuilder().build(Types.VARCHAR, sbPass.toString(), quoteName(getConfiguration().getQuoting(), COLUMN_WITH_PASSWORD).toLowerCase()));
			}else if(attr.getName().equalsIgnoreCase(Name.NAME)){
				requestBuilder.setNameAndValueOfColumn(new SQLParameterBuilder().build(Types.VARCHAR, ((Name)attr).getNameValue(), quoteName(getConfiguration().getQuoting(), COLUMN_WITH_NAME).toLowerCase()));
			}else{
				requestBuilder.setNameAndValueOfColumn(new SQLParameterBuilder().build(getSqlTypes().get(attr.getName()), attr.getValue().get(0), quoteName(getConfiguration().getQuoting(), attr.getName())));
			}
		}
		
		return requestBuilder.build();
	} 
	
	@Override
	public void test() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM ").append(TABLE_OF_ACCOUNTS).append(" WHERE ").append(KEY_OF_ACCOUNTS_TABLE).append(" IS NULL");
		String sql = sb.toString();
		
		Statement stmt = null;
		try {
			stmt = getConnection().createStatement();
			stmt.executeQuery(sql);
		} catch (SQLException ex) {
			LOGGER.error(ex.getMessage());
			if(rethrowSQLException(ex.getErrorCode())){
				throw new ConnectorException(ex.getMessage(), ex);
			}
		}
	}

	@Override
	public Uid update(ObjectClass objectClass, Uid uid, Set<Attribute> attributes, OperationOptions option) {
		if (objectClass == null) {
			LOGGER.error("Attribute of type ObjectClass not provided.");
			throw new InvalidAttributeValueException("Attribute of type ObjectClass not provided.");
		}
		if (attributes == null) {
			LOGGER.error("Attribute of type Set<Attribute> not provided.");
			throw new InvalidAttributeValueException("Attribute of type Set<Attribute> not provided.");
		}
		if (option == null) {
			LOGGER.error("Attribute of type OperationOptions not provided.");
}}}