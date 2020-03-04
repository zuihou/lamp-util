import com.github.zuihou.database.parsers.MultiTenantInterceptor;

public class DataBaseTest {

    public static void main(String[] args) {
        // 复杂sql ，包含函数
        String sql = "SELECT `id`, (SELECT GROUP_CONCAT(IF(p2.shortname IS NULL || LENGTH(TRIM(p2.shortname)) < 1,p2.name, p2.shortname) SEPARATOR ' ')\n" +
                "        FROM places AS p2 WHERE FIND_IN_SET(p2.id,SUBSTRING_INDEX(places.tree_id, ',', -(places.level-1+1)))\n" +
                "        ORDER BY p2.`level` ASC) AS fullName FROM `places` WHERE (FIND_IN_SET(1563, places.tree_id))\n" +
                "        AND `places`.`id` = 1563 AND `places`.`deleted_at` IS NULL";

        // 简单sql
//        sql = "SELECT browser, count(id) AS `count` FROM c_common_login_log GROUP BY browser";
//         sql = "select * from user id &lt;  2" ;
        // 存储过程
//        sql = "select get_base(2,1)";

        MultiTenantInterceptor i = new MultiTenantInterceptor();
        i.setSchemaName("base_9");
        String s = i.processSqlByInterceptor(sql);
        System.out.println(s);
    }
}
