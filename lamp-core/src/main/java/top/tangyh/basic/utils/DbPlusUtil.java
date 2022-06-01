package top.tangyh.basic.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.meta.MetaUtil;
import cn.hutool.db.meta.Table;
import cn.hutool.db.meta.TableType;
import com.baomidou.mybatisplus.annotation.DbType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库工具类
 *
 * @author zuihou
 * @date 2021/11/3 18:32
 */
public class DbPlusUtil {
    /**
     * 截取jdbc地址中的数据库名
     *
     * @param jdbcUrl
     * @return
     */
    public static String getDataBaseNameByUrl(String jdbcUrl) {
        String database = null;
        int pos, pos1;
        String connUri;

        if (StrUtil.isBlank(jdbcUrl)) {
            throw new IllegalArgumentException("Invalid JDBC url.");
        }

        jdbcUrl = jdbcUrl.toLowerCase();

        if (jdbcUrl.startsWith("jdbc:impala")) {
            jdbcUrl = jdbcUrl.replace(":impala", "");
        }

        if (!jdbcUrl.startsWith("jdbc:")
                || (pos1 = jdbcUrl.indexOf(':', 5)) == -1) {
            throw new IllegalArgumentException("Invalid JDBC url.");
        }

        connUri = jdbcUrl.substring(pos1 + 1);

        if (connUri.startsWith("//")) {
            if ((pos = connUri.indexOf('/', 2)) != -1) {
                database = connUri.substring(pos + 1);
            }
        } else {
            database = connUri;
        }

        if (database.contains("?")) {
            database = database.substring(0, database.indexOf("?"));
        }

        if (database.contains(";")) {
            database = database.substring(0, database.indexOf(";"));
        }

        if (StrUtil.isBlank(database)) {
            throw new IllegalArgumentException("Invalid JDBC url.");
        }
        return database;
    }

    /**
     * 获得所有表名
     *
     * @param ds 数据源
     * @return 表名列表
     */
    public static List<Table> getTables(DataSource ds) {
        final List<Table> tables = new ArrayList<>();
        Connection conn = null;
        try {
            conn = ds.getConnection();
            // catalog和schema获取失败默认使用null代替
            String catalog = MetaUtil.getCataLog(conn);
            String schema = MetaUtil.getSchema(conn);


            final DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet rs = metaData.getTables(catalog, schema, null, Convert.toStrArray(TableType.TABLE))) {
                if (null != rs) {
                    String tableName;
                    while (rs.next()) {
                        tableName = rs.getString("TABLE_NAME");
                        if (StrUtil.isNotBlank(tableName)) {
                            final Table table = Table.create(tableName);
                            table.setCatalog(catalog);
                            table.setSchema(schema);
                            table.setComment(rs.getString("REMARKS"));
                            // 获得主键
                            try (ResultSet rsPk = metaData.getPrimaryKeys(catalog, schema, tableName)) {
                                if (null != rsPk) {
                                    while (rsPk.next()) {
                                        table.addPk(rsPk.getString("COLUMN_NAME"));
                                    }
                                }
                            }
                            tables.add(table);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new DbRuntimeException("Get tables error!", e);
        } finally {
            cn.hutool.db.DbUtil.close(conn);
        }
        return tables;
    }

    /**
     * 根据url获取数据库类型
     *
     * @param url jdbc 链接地址
     * @return MybatisPlus 支持的数据库类型,主要用于分页方言
     */
    public static DbType getDbType(String url) {
        if (url.contains(":mysql:") || url.contains(":cobar:")) {
            return DbType.MYSQL;
        } else if (url.contains(":oracle:")) {
            return DbType.ORACLE;
        } else if (url.contains(":postgresql:")) {
            return DbType.POSTGRE_SQL;
        } else if (url.contains(":sqlserver:")) {
            return DbType.SQL_SERVER;
        } else if (url.contains(":db2:")) {
            return DbType.DB2;
        } else if (url.contains(":mariadb:")) {
            return DbType.MARIADB;
        } else if (url.contains(":sqlite:")) {
            return DbType.SQLITE;
        } else if (url.contains(":h2:")) {
            return DbType.H2;
        } else if (url.contains(":kingbase:") || url.contains(":kingbase8:")) {
            return DbType.KINGBASE_ES;
        } else if (url.contains(":dm:")) {
            return DbType.DM;
        } else if (url.contains(":zenith:")) {
            return DbType.GAUSS;
        } else if (url.contains(":oscar:")) {
            return DbType.OSCAR;
        } else if (url.contains(":firebird:")) {
            return DbType.FIREBIRD;
        } else if (url.contains(":xugu:")) {
            return DbType.XU_GU;
        } else if (url.contains(":clickhouse:")) {
            return DbType.CLICK_HOUSE;
        } else if (url.contains(":sybase:")) {
            return DbType.SYBASE;
        } else {
            return DbType.OTHER;
        }
    }
}
