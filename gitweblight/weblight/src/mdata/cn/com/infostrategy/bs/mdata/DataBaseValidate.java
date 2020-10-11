package cn.com.infostrategy.bs.mdata;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TableDataStruct;

/**
 * 数据库验证 校验所有表名,字段名是否合法,不允许出现关键字
 * 
 * @author xch
 * 
 */
public class DataBaseValidate {

	private String[] str_oracle = new String[] { " ", "  ", "    ", "      ", "&", "|", ":", ",", "-", "=", ">", "[", "<", "(", ".", "+", "]", ")", "!", "/", "*", "^", "@", "ABORT", "ACCESS", "ACCESSED", "ACCOUNT", "ACTIVATE", "ADD", "ADMIN", "ADMINISTER", "ADMINISTRATOR", "ADVISE", "AFTER",
			"ALGORITHM", "ALIAS", "ALL", "ALL_ROWS", "ALLOCATE", "ALLOW", "ALTER", "ALWAYS", "ANALYZE", "ANCILLARY", "AND", "ANY", "APPLY", "ARCHIVE", "ARCHIVELOG", "ARRAY", "AS", "ASC", "ASSOCIATE", "AT", "ATTRIBUTE", "ATTRIBUTES", "AUDIT", "AUTHENTICATED", "AUTHID", "AUTHORIZATION", "AUTO",
			"AUTOALLOCATE", "AUTOEXTEND", "AUTOMATIC", "AVAILABILITY", "BACKUP", "BECOME", "BEFORE", "BEGIN", "BEHALF", "BETWEEN", "BFILE", "BINDING", "BITMAP", "BITS", "BLOB", "BLOCK", "BLOCKSIZE", "BLOCK_RANGE", "BODY", "BOUND", "BOTH", "BROADCAST", "BUFFER_POOL", "BUILD", "BULK", "BY", "BYTE",
			"CACHE", "CACHE_INSTANCES", "CALL", "CANCEL", "CASCADE", "CASE", "CAST", "CATEGORY", "CERTIFICATE", "CFILE", "CHAINED", "CHANGE", "CHAR", "CHAR_CS", "CHARACTER", "CHECK", "CHECKPOINT", "CHILD", "CHOOSE", "CHUNK", "CLASS", "CLEAR", "CLOB", "CLONE", "CLOSE", "CLOSE_CACHED_OPEN_CURSORS",
			"CLUSTER", "COALESCE", "COLLECT", "COLUMN", "COLUMNS", "COLUMN_VALUE", "COMMENT", "COMMIT", "COMMITTED", "COMPATIBILITY", "COMPILE", "COMPLETE", "COMPOSITE_LIMIT", "COMPRESS", "COMPUTE", "CONFORMING", "CONNECT", "CONNECT_TIME", "CONSIDER", "CONSISTENT", "CONSTRAINT", "CONSTRAINTS",
			"CONTAINER", "CONTENTS", "CONTEXT", "CONTINUE", "CONTROLFILE", "CONVERT", "CORRUPTION", "COST", "CPU_PER_CALL", "CPU_PER_SESSION", "CREATE", "CREATE_STORED_OUTLINES", "CROSS", "CUBE", "CURRENT", "CURRENT_DATE", "CURRENT_SCHEMA", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER",
			"CURSOR", "CURSOR_SPECIFIC_SEGMENT", "CYCLE", "DANGLING", "DATA", "DATABASE", "DATAFILE", "DATAFILES", "DATAOBJNO", "DATE", "DATE_MODE", "DAY", "DBA", "DBTIMEZONE", "DDL", "DEALLOCATE", "DEBUG", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DEFERRABLE", "DEFERRED", "DEFINED", "DEFINER",
			"DEGREE", "DELAY", "DELETE", "DEMAND", "DENSE_RANK", "ROWDEPENDENCIES", "DEREF", "DESC", "DETACHED", "DETERMINES", "DICTIONARY", "DIMENSION", "DIRECTORY", "DISABLE", "DISASSOCIATE", "DISCONNECT", "DISK", "DISKGROUP", "DISKS", "DISMOUNT", "DISPATCHERS", "DISTINCT", "DISTINGUISHED",
			"DISTRIBUTED", "DML", "DOUBLE", "DROP", "DUMP", "DYNAMIC", "EACH", "ELEMENT", "ELSE", "ENABLE", "ENCRYPTED", "ENCRYPTION", "END", "ENFORCE", "ENTRY", "ERROR_ON_OVERLAP_TIME", "ESCAPE", "ESTIMATE", "EVENTS", "EXCEPT", "EXCEPTIONS", "EXCHANGE", "EXCLUDING", "EXCLUSIVE", "EXECUTE",
			"EXEMPT", "EXISTS", "EXPIRE", "EXPLAIN", "EXPLOSION", "EXTEND", "EXTENDS", "EXTENT", "EXTENTS", "EXTERNAL", "EXTERNALLY", "EXTRACT", "FAILED_LOGIN_ATTEMPTS", "FAILGROUP", "FALSE", "FAST", "FILE", "FILTER", "FINAL", "FINISH", "FIRST", "FIRST_ROWS", "FLAGGER", "FLASHBACK", "FLOAT",
			"FLOB", "FLUSH", "FOLLOWING", "FOR", "FORCE", "FOREIGN", "FREELIST", "FREELISTS", "FREEPOOLS", "FRESH", "FROM", "FULL", "FUNCTION", "FUNCTIONS", "GENERATED", "GLOBAL", "GLOBALLY", "GLOBAL_NAME", "GLOBAL_TOPIC_ENABLED", "GRANT", "GROUP", "GROUPING", "GROUPS", "GUARANTEED", "GUARD",
			"HASH", "HASHKEYS", "HAVING", "HEADER", "HEAP", "HIERARCHY", "HOUR", "ID", "IDENTIFIED", "IDENTIFIER", "IDGENERATORS", "IDLE_TIME", "IF", "IMMEDIATE", "IN", "INCLUDING", "INCREMENT", "INCREMENTAL", "INDEX", "INDEXED", "INDEXES", "INDEXTYPE", "INDEXTYPES", "INDICATOR", "INITIAL",
			"INITIALIZED", "INITIALLY", "INITRANS", "INNER", "INSERT", "INSTANCE", "INSTANCES", "INSTANTIABLE", "INSTANTLY", "INSTEAD", "INT", "INTEGER", "INTEGRITY", "INTERMEDIATE", "INTERNAL_USE", "INTERNAL_CONVERT", "INTERSECT", "INTERVAL", "INTO", "INVALIDATE", "IN_MEMORY_METADATA", "IS",
			"ISOLATION", "ISOLATION_LEVEL", "JAVA", "JOIN", "KEEP", "KERBEROS", "KEY", "KEYFILE", "KEYS", "KEYSIZE", "REKEY", "KILL", "<<", "LAST", "LATERAL", "LAYER", "LDAP_REGISTRATION", "LDAP_REGISTRATION_ENABLED", "LDAP_REG_SYNC_INTERVAL", "LEADING", "LEFT", "LESS", "LEVEL", "LEVELS",
			"LIBRARY", "LIKE", "LIKE2", "LIKE4", "LIKEC", "LIMIT", "LINK", "LIST", "LOB", "LOCAL", "LOCALTIME", "LOCALTIMESTAMP", "LOCATION", "LOCATOR", "LOCK", "LOCKED", "LOG", "LOGFILE", "LOGGING", "LOGICAL", "LOGICAL_READS_PER_CALL", "LOGICAL_READS_PER_SESSION", "LOGOFF", "LOGON", "LONG",
			"MANAGE", "MANAGED", "MANAGEMENT", "MANUAL", "MAPPING", "MASTER", "MATERIALIZED", "MATCHED", "MAX", "MAXARCHLOGS", "MAXDATAFILES", "MAXEXTENTS", "MAXIMIZE", "MAXINSTANCES", "MAXLOGFILES", "MAXLOGHISTORY", "MAXLOGMEMBERS", "MAXSIZE", "MAXTRANS", "MAXVALUE", "METHOD", "MIN", "MEMBER",
			"MEMORY", "MERGE", "MIGRATE", "MINIMIZE", "MINIMUM", "MINEXTENTS", "MINUS", "MINUTE", "MINVALUE", "MIRROR", "MLSLABEL", "MODE", "MODIFY", "MONITORING", "MONTH", "MOUNT", "MOVE", "MOVEMENT", "MTS_DISPATCHERS", "MULTISET", "NAME", "NAMED", "NATIONAL", "NATURAL", "NCHAR", "NCHAR_CS",
			"NCLOB", "NEEDED", "NESTED", "NESTED_TABLE_ID", "NETWORK", "NEVER", "NEW", "NEXT", "NLS_CALENDAR", "NLS_CHARACTERSET", "NLS_COMP", "NLS_NCHAR_CONV_EXCP", "NLS_CURRENCY", "NLS_DATE_FORMAT", "NLS_DATE_LANGUAGE", "NLS_ISO_CURRENCY", "NLS_LANG", "NLS_LANGUAGE", "NLS_LENGTH_SEMANTICS",
			"NLS_NUMERIC_CHARACTERS", "NLS_SORT", "NLS_SPECIAL_CHARS", "NLS_TERRITORY", "NO", "NOARCHIVELOG", "NOAUDIT", "NOCACHE", "NOCOMPRESS", "NOCYCLE", "NOROWDEPENDENCIES", "NODELAY", "NOFORCE", "NOLOGGING", "NOMAPPING", "NOMAXVALUE", "NOMINIMIZE", "NOMINVALUE", "NOMONITORING", "NONE",
			"NOORDER", "NOOVERRIDE", "NOPARALLEL", "NORELY", "NOREPAIR", "NORESETLOGS", "NOREVERSE", "NORMAL", "NOSEGMENT", "NOSTRICT", "NOSTRIPE", "NOSORT", "NOSWITCH", "NOT", "NOTHING", "NOVALIDATE", "NOWAIT", "NULL", "NULLS", "NUMBER", "NUMERIC", "NVARCHAR2", "OBJECT", "OBJNO", "OBJNO_REUSE",
			"OF", "OFF", "OFFLINE", "OID", "OIDINDEX", "OLD", "ON", "ONLINE", "ONLY", "OPAQUE", "OPCODE", "OPEN", "OPERATOR", "OPTIMAL", "OPTIMIZER_GOAL", "OPTION", "OR", "ORDER", "ORGANIZATION", "OUTER", "OUTLINE", "OVER", "OVERFLOW", "OVERLAPS", "OWN", "PACKAGE", "PACKAGES", "PARALLEL",
			"PARAMETERS", "PARENT", "PARITY", "PARTIALLY", "PARTITION", "PARTITIONS", "PARTITION_HASH", "PARTITION_LIST", "PARTITION_RANGE", "PASSWORD", "PASSWORD_GRACE_TIME", "PASSWORD_LIFE_TIME", "PASSWORD_LOCK_TIME", "PASSWORD_REUSE_MAX", "PASSWORD_REUSE_TIME", "PASSWORD_VERIFY_FUNCTION",
			"PCTFREE", "PCTINCREASE", "PCTTHRESHOLD", "PCTUSED", "PCTVERSION", "PERCENT", "PERFORMANCE", "PERMANENT", "PFILE", "PHYSICAL", "PLAN", "PLSQL_DEBUG", "POLICY", "POST_TRANSACTION", "PREBUILT", "PRECEDING", "PRECISION", "PREPARE", "PRESERVE", "PRIMARY", "PRIOR", "PRIVATE", "PRIVATE_SGA",
			"PRIVILEGE", "PRIVILEGES", "PROCEDURE", "PROFILE", "PROTECTED", "PROTECTION", "PUBLIC", "PURGE", "PX_GRANULE", "QUERY", "QUEUE", "QUIESCE", "QUOTA", "RANDOM", "RANGE", "RAPIDLY", "RAW", "RBA", "READ", "READS", "REAL", "REBALANCE", "REBUILD", "RECORDS_PER_BLOCK", "RECOVER",
			"RECOVERABLE", "RECOVERY", "RECYCLE", "REDUCED", "REF", "REFERENCES", "REFERENCING", "REFRESH", "REGISTER", "REJECT", "RELATIONAL", "RELY", "RENAME", "REPAIR", "REPLACE", "RESET", "RESETLOGS", "RESIZE", "RESOLVE", "RESOLVER", "RESOURCE", "RESTRICT", "RESTRICTED", "RESUMABLE", "RESUME",
			"RETENTION", "RETURN", "RETURNING", "REUSE", "REVERSE", "REVOKE", "REWRITE", "RIGHT", "ROLE", "ROLES", "ROLLBACK", "ROLLUP", "ROW", "ROWID", "ROWNUM", "ROWS", "RULE", "SAMPLE", "SAVEPOINT", "SB4", "SCAN", "SCAN_INSTANCES", "SCHEMA", "SCN", "SCOPE", "SD_ALL", "SD_INHIBIT", "SD_SHOW",
			"SECOND", "SECURITY", "SEGMENT", "SEG_BLOCK", "SEG_FILE", "SELECT", "SELECTIVITY", "SEQUENCE", "SEQUENCED", "SERIALIZABLE", "SERVERERROR", "SESSION", "SESSION_CACHED_CURSORS", "SESSIONS_PER_USER", "SESSIONTIMEZONE", "SESSIONTZNAME", "SET", "SETS", "SETTINGS", "SHARE", "SHARED",
			"SHARED_POOL", "SHRINK", "SHUTDOWN", "SIBLINGS", "SID", "SINGLE", "SINGLETASK", "SIMPLE", "SIZE", "SKIP", "SKIP_UNUSABLE_INDEXES", "SMALLINT", "SNAPSHOT", "SOME", "SORT", "SOURCE", "SPACE", "SPECIFICATION", "SPFILE", "SPLIT", "SQL_TRACE", "STANDBY", "START", "STARTUP", "STATEMENT_ID",
			"STATISTICS", "STATIC", "STOP", "STORAGE", "STORE", "STRIPE", "STRICT", "STRUCTURE", "SUBPARTITION", "SUBPARTITIONS", "SUBPARTITION_REL", "SUBSTITUTABLE", "SUCCESSFUL", "SUMMARY", "SUSPEND", "SUPPLEMENTAL", "SWITCH", "SWITCHOVER", "SYS_OP_BITVEC", "SYS_OP_COL_PRESENT",
			"SYS_OP_ENFORCE_NOT_NULL$", "SYS_OP_MINE_VALUE", "SYS_OP_NOEXPAND", "SYS_OP_NTCIMG$", "SYNONYM", "SYSDATE", "SYSDBA", "SYSOPER", "SYSTEM", "SYSTIMESTAMP", "TABLE", "TABLES", "TABLESPACE", "TABLESPACE_NO", "TABNO", "TEMPFILE", "TEMPLATE", "TEMPORARY", "TEST", "THAN", "THE", "THEN",
			"THREAD", "THROUGH", "TIMESTAMP", "TIME", "TIMEOUT", "TIMEZONE_ABBR", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TIMEZONE_REGION", "TIME_ZONE", "TO", "TOPLEVEL", "TRACE", "TRACING", "TRAILING", "TRANSACTION", "TRANSITIONAL", "TREAT", "TRIGGER", "TRIGGERS", "TRUE", "TRUNCATE", "TX", "TYPE",
			"TYPES", "TZ_OFFSET", "UB2", "UBA", "UID", "UNARCHIVED", "UNBOUND", "UNBOUNDED", "UNDER", "UNDO", "UNDROP", "UNIFORM", "UNION", "UNIQUE", "UNLIMITED", "UNLOCK", "UNPACKED", "UNPROTECTED", "UNQUIESCE", "UNRECOVERABLE", "UNTIL", "UNUSABLE", "UNUSED", "UPD_INDEXES", "UPD_JOININDEX",
			"UPDATABLE", "UPDATE", "UPGRADE", "UROWID", "USAGE", "USE", "USE_PRIVATE_OUTLINES", "USE_STORED_OUTLINES", "USER", "USER_DEFINED", "USING", "VALIDATE", "VALIDATION", "VALUE", "VALUES", "VARCHAR", "VARCHAR2", "VARRAY", "VARYING", "VERSION", "VIEW", "WAIT", "WHEN", "WHENEVER", "WHERE",
			"WITH", "WITHIN", "WITHOUT", "WORK", "WRITE", "XMLATTRIBUTES", "XMLCOLATTVAL", "XMLELEMENT", "XMLFOREST", "XMLTYPE", "XMLSCHEMA", "XID", "YEAR", "ZONE" }; // 碰撞的关键字
	private String[] str_sqlserver = new String[] { "ADD", "EXCEPT", "PERCENT", "ALL", "EXEC", "PLAN", "ALTER", "EXECUTE", "PRECISION", "AND", "EXISTS", "PRIMARY", "ANY", "EXIT", "PRINT", "AS", "FETCH", "PROC", "ASC", "FILE", "PROCEDURE", "AUTHORIZATION", "FILLFACTOR", "PUBLIC", "BACKUP", "FOR",
			"RAISERROR", "BEGIN", "FOREIGN", "READ", "BETWEEN", "FREETEXT", "READTEXT", "BREAK", "FREETEXTTABLE", "RECONFIGURE", "BROWSE", "FROM", "REFERENCES", "BULK", "FULL", "REPLICATION", "BY", "FUNCTION", "RESTORE", "CASCADE", "GOTO", "RESTRICT", "CASE", "GRANT", "RETURN", "CHECK", "GROUP",
			"REVOKE", "CHECKPOINT", "HAVING", "RIGHT", "CLOSE", "HOLDLOCK", "ROLLBACK", "CLUSTERED", "IDENTITY", "ROWCOUNT", "COALESCE", "IDENTITY_INSERT", "ROWGUIDCOL", "COLLATE", "IDENTITYCOL", "RULE", "COLUMN", "IF", "SAVE", "COMMIT", "IN", "SCHEMA", "COMPUTE", "INDEX", "SELECT", "CONSTRAINT",
			"INNER", "SESSION_USER", "CONTAINS", "INSERT", "SET", "CONTAINSTABLE", "INTERSECT", "SETUSER", "CONTINUE", "INTO", "SHUTDOWN", "CONVERT", "IS", "SOME", "CREATE", "JOIN", "STATISTICS", "CROSS", "KEY", "SYSTEM_USER", "CURRENT", "KILL", "TABLE", "CURRENT_DATE", "LEFT", "TEXTSIZE",
			"CURRENT_TIME", "LIKE", "THEN", "CURRENT_TIMESTAMP", "LINENO", "TO", "CURRENT_USER", "LOAD", "TOP", "CURSOR", "NATIONAL", "TRAN", "DATABASE", "NOCHECK", "TRANSACTION", "DBCC", "NONCLUSTERED", "TRIGGER", "DEALLOCATE", "NOT", "TRUNCATE", "DECLARE", "NULL", "TSEQUAL", "DEFAULT", "NULLIF",
			"UNION", "DELETE", "OF", "UNIQUE", "DENY", "OFF", "UPDATE", "DESC", "OFFSETS", "UPDATETEXT", "DISK", "ON", "USE", "DISTINCT", "OPEN", "USER", "DISTRIBUTED", "OPENDATASOURCE", "VALUES", "DOUBLE", "OPENQUERY", "VARYING", "DROP", "OPENROWSET", "VIEW", "DUMMY", "OPENXML", "WAITFOR", "DUMP",
			"OPTION", "WHEN", "ELSE", "OR", "WHERE", "END", "ORDER", "WHILE", "ERRLVL", "OUTER", "WITH", "ESCAPE", "OVER", "WRITETEXT" }; //
	private String[] str_mysql = new String[] { "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "ASENSITIVE", "BEFORE", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE", "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "CONDITION", "CONNECTION",
			"CONSTRAINT", "CONTINUE", "CONVERT", "CREATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATABASE", "DATABASES", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DESC",
			"DESCRIBE", "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE", "DROP", "DUAL", "EACH", "ELSE", "ELSEIF", "ENCLOSED", "ESCAPED", "EXISTS", "EXIT", "EXPLAIN", "FALSE", "FETCH", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT", "GOTO", "GRANT", "GROUP",
			"HAVING", "HIGH_PRIORITY", "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "IN", "INDEX", "INFILE", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERVAL", "INTO", "IS", "ITERATE", "JOIN", "KEY", "KEYS", "KILL",
			"LABEL", "LEADING", "LEAVE", "LEFT", "LIKE", "LIMIT", "LINEAR", "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB", "LONGTEXT", "LOOP", "LOW_PRIORITY", "MATCH", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD",
			"MODIFIES", "NATURAL", "NOT", "NO_WRITE_TO_BINLOG", "NULL", "NUMERIC", "ON", "OPTIMIZE", "OPTION", "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", "PRECISION", "PRIMARY", "PROCEDURE", "PURGE", "RAID0", "RANGE", "READ", "READS", "REAL", "REFERENCES", "REGEXP", "RELEASE",
			"RENAME", "REPEAT", "REPLACE", "REQUIRE", "RESTRICT", "RETURN", "REVOKE", "RIGHT", "RLIKE", "SCHEMA", "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW", "SMALLINT", "SPATIAL", "SPECIFIC", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQL_BIG_RESULT",
			"SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", "SSL", "STARTING", "STRAIGHT_JOIN", "TABLE", "TERMINATED", "THEN", "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING", "TRIGGER", "TRUE", "UNDO", "UNION", "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE", "USAGE", "USE", "USING", "UTC_DATE",
			"UTC_TIME", "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", "WHEN", "WHERE", "WHILE", "WITH", "WRITE", "X509", "XOR", "YEAR_MONTH", "ZEROFILL" }; //
	private String[] str_db2 = new String[] { "checkpoint", "istishen" }; //

	/**
	 * 得到当前系统表所有和关键字碰撞的字段
	 * 
	 * @param _databasetype
	 * @return
	 * @throws Exception
	 */
	public String getAllCollidePK(String _databasetype) throws Exception {
		CommDMO commDMO = new CommDMO();
		StringBuffer sb_primarykey = new StringBuffer();
		if (_databasetype == null) {
			_databasetype = ServerEnvironment.getInstance().getDefaultDataSourceType();
		}
		HashVO[] hashvo = commDMO.getHashVoArrayByDS(null, "select * from v_pub_syscolumns");
		if ("ORACLE".equalsIgnoreCase(_databasetype)) {
			for (int i = 0; i < str_oracle.length; i++) {
				for (int j = 0; j < hashvo.length; j++) {
					if (str_oracle[i].equalsIgnoreCase(hashvo[j].getStringValue("COLNAME")) && !"id".equalsIgnoreCase(hashvo[j].getStringValue("COLNAME"))) {
						sb_primarykey.append("表：【" + hashvo[j].getStringValue("tabname") + "】字段：【" + hashvo[j].getStringValue("COLNAME") + "】为【" + _databasetype + "】的关键字");
						sb_primarykey.append("\r\n");
					}
				}
			}

		} else if ("SQLSERVER".equalsIgnoreCase(_databasetype)) {
			for (int i = 0; i < str_sqlserver.length; i++) {
				for (int j = 0; j < hashvo.length; j++) {
					if (str_sqlserver[i].equalsIgnoreCase(hashvo[j].getStringValue("COLNAME")) && !"id".equalsIgnoreCase(hashvo[j].getStringValue("COLNAME"))) {
						sb_primarykey.append("表：【" + hashvo[j].getStringValue("tabname") + "】字段：【" + hashvo[j].getStringValue("COLNAME") + "】为【" + _databasetype + "】的关键字");
						sb_primarykey.append("\r\n");
					}
				}
			}
		} else if ("MYSQL".equalsIgnoreCase(_databasetype)) {
			for (int i = 0; i < str_mysql.length; i++) {
				for (int j = 0; j < hashvo.length; j++) {
					if (str_mysql[i].equalsIgnoreCase(hashvo[j].getStringValue("COLNAME")) && !"id".equalsIgnoreCase(hashvo[j].getStringValue("COLNAME"))) {
						sb_primarykey.append("表：【" + hashvo[j].getStringValue("tabname") + "】字段：【" + hashvo[j].getStringValue("COLNAME") + "】为【" + _databasetype + "】的关键字");
						sb_primarykey.append("\r\n");
					}
				}
			}

		} else if ("DB2".equalsIgnoreCase(_databasetype)) {
			for (int i = 0; i < str_db2.length; i++) {
				for (int j = 0; j < hashvo.length; j++) {
					if (str_db2[i].equalsIgnoreCase(hashvo[j].getStringValue("COLNAME")) && !"id".equalsIgnoreCase(hashvo[j].getStringValue("COLNAME"))) {
						sb_primarykey.append("表：【" + hashvo[j].getStringValue("tabname") + "】字段：【" + hashvo[j].getStringValue("COLNAME") + "】为【" + _databasetype + "】的关键字");
						sb_primarykey.append("\r\n");
					}
				}
			}
		}

		return sb_primarykey.toString();
	}

	/**
	 * 根据表名得到碰撞的字符串，返回一个字符串
	 * 
	 * @param _tablename
	 *            表名
	 * @param _databasetype
	 *            数据库类型
	 * @return
	 * @throws Exception
	 */
	public String getCollidePKByTableName(String[] _tablename, String _databasetype) throws Exception {
		CommDMO commDMO = new CommDMO();
		if (_databasetype == null) {
			_databasetype = ServerEnvironment.getInstance().getDefaultDataSourceType();
		}
		StringBuffer sb_primarykey = new StringBuffer();
		if ("ORACLE".equalsIgnoreCase(_databasetype)) {
			for (int i = 0; i < _tablename.length; i++) {
				TableDataStruct tablebs = commDMO.getTableDataStructByDS(null, "select * from " + _tablename[i]);
				for (int j = 0; j < tablebs.getHeaderName().length; j++) {
					for (int k = 0; k < str_oracle.length; k++) {
						if (str_oracle[k].equalsIgnoreCase(tablebs.getHeaderName()[j]) && !"id".equalsIgnoreCase(tablebs.getHeaderName()[j])) {
							sb_primarykey.append("表：【" + _tablename[i] + "】字段：【" + tablebs.getHeaderName()[j] + "】为【" + _databasetype + "】的关键字");
							sb_primarykey.append("\r\n");
						}
					}
				}
			}

		} else if ("SQLSERVER".equalsIgnoreCase(_databasetype)) {
			for (int i = 0; i < _tablename.length; i++) {
				TableDataStruct tablebs = commDMO.getTableDataStructByDS(null, "select * from " + _tablename[i]);
				for (int j = 0; j < tablebs.getHeaderName().length; j++) {
					for (int k = 0; k < str_sqlserver.length; k++) {
						if (str_sqlserver[k].equalsIgnoreCase(tablebs.getHeaderName()[j]) && !"id".equalsIgnoreCase(tablebs.getHeaderName()[j])) {
							sb_primarykey.append("表：【" + _tablename[i] + "】字段：【" + tablebs.getHeaderName()[j] + "】为【" + _databasetype + "】的关键字");
							sb_primarykey.append("\r\n");
						}
					}
				}
			}

		} else if ("MYSQL".equalsIgnoreCase(_databasetype)) {
			for (int i = 0; i < _tablename.length; i++) {
				TableDataStruct tablebs = commDMO.getTableDataStructByDS(null, "select * from " + _tablename[i]);
				for (int j = 0; j < tablebs.getHeaderName().length; j++) {
					for (int k = 0; k < str_mysql.length; k++) {
						if (str_mysql[k].equalsIgnoreCase(tablebs.getHeaderName()[j]) && !"id".equalsIgnoreCase(tablebs.getHeaderName()[j])) {
							sb_primarykey.append("表：【" + _tablename[i] + "】字段：【" + tablebs.getHeaderName()[j] + "】为【" + _databasetype + "】的关键字");
							sb_primarykey.append("\r\n");
						}
					}
				}
			}

		} else if ("DB2".equalsIgnoreCase(_databasetype)) {
			for (int i = 0; i < _tablename.length; i++) {
				TableDataStruct tablebs = commDMO.getTableDataStructByDS(null, "select * from " + _tablename[i]);
				for (int j = 0; j < tablebs.getHeaderName().length; j++) {
					for (int k = 0; k < str_db2.length; k++) {
						if (str_db2[k].equalsIgnoreCase(tablebs.getHeaderName()[j]) && !"id".equalsIgnoreCase(tablebs.getHeaderName()[j])) {
							sb_primarykey.append("表：【" + _tablename[i] + "】字段：【" + tablebs.getHeaderName()[j] + "】为【" + _databasetype + "】的关键字");
							sb_primarykey.append("\r\n");
						}
					}
				}
			}
		}
		return sb_primarykey.toString();
	}

	/**
	 * 根据字段名称判断是否是关键字，这里需要在html中展现，并且只需要说明即可【李春娟/2013-05-24】
	 * 
	 * @param _colname
	 * @return
	 * @throws Exception
	 */
	public String checkValidateByCol(String _colname) {
		StringBuffer sb_primarykey = new StringBuffer();
		for (int i = 0; i < str_oracle.length; i++) {
			if (str_oracle[i].equalsIgnoreCase(_colname) && !"id".equalsIgnoreCase(_colname)) {
				sb_primarykey.append("字段【" + _colname + "】为ORACLE的关键字<br>");
			}
		}

		for (int i = 0; i < str_sqlserver.length; i++) {
			if (str_sqlserver[i].equalsIgnoreCase(_colname) && !"id".equalsIgnoreCase(_colname)) {
				sb_primarykey.append("字段【" + _colname + "】为SQLSERVER的关键字<br>");
			}
		}
		for (int i = 0; i < str_mysql.length; i++) {
			if (str_mysql[i].equalsIgnoreCase(_colname) && !"id".equalsIgnoreCase(_colname)) {
				sb_primarykey.append("字段【" + _colname + "】为MYSQL的关键字<br>");
			}
		}

		for (int i = 0; i < str_db2.length; i++) {
			if (str_db2[i].equalsIgnoreCase(_colname) && !"id".equalsIgnoreCase(_colname)) {
				sb_primarykey.append("字段【" + _colname + "】为DB2的关键字<br>");
			}
		}
		return sb_primarykey.toString();
	}
}
