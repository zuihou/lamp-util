import com.github.zuihou.database.parsers.ReplaceSql;

public class DataBaseTest {


    public static void main(String[] args) {
//        String field = "name";
//        System.out.println(getField(MenuTreeDTO.class, field));
//
//        System.out.println(ReflectUtil.getField(MenuTreeDTO.class, field));


        String sql = "       SELECT u.id, account, name, mobile, sex\n" +
                "FROM c_auth_user\n" +
                "u\n" +
                "WHERE 1=1\n" +
                "and EXISTS (\n" +
                "select 1 from c_auth_user_role\n" +
                "ur where  u.id = ur.user_id\n" +
                "and ur.role_id = 100\n" +
                ")";
        // 复杂sql ，包含函数
        sql = "SELECT `id`, (SELECT GROUP_CONCAT(IF(p2.shortname IS NULL || LENGTH(TRIM(p2.shortname)) < 1,p2.name, p2.shortname) SEPARATOR ' ')\n" +
                "        FROM places AS p2 WHERE FIND_IN_SET(p2.id,SUBSTRING_INDEX(places.tree_id, ',', -(places.level-1+1)))\n" +
                "        ORDER BY p2.`level` ASC) AS fullName FROM `places` WHERE (FIND_IN_SET(1563, places.tree_id))\n" +
                "        AND `places`.`id` = 1563 AND `places`.`deleted_at` IS NULL";


        // 简单sql
        sql = "SELECT browser, count(id) AS `count` FROM c_common_login_log GROUP BY browser";
        sql = "select * from user id &lt;  2";

        sql = "insert into c_auth_resource ( id, create_user, create_time, update_user, update_time, code, name, menu_id, describe_)\n" +
                "    values (1, 2, SYSDATE(), 2,SYSDATE(), 'code', 'name', 1, ''\t\t)\n" +
                "    ON DUPLICATE KEY UPDATE " +
                "      name = 'name2',\n" +
                "      describe_ = 'ddd',\n" +
                "      update_user = 3,\n" +
                "      update_time = SYSDATE()";
        sql = "CREATE TABLE `aaa_ba`  (\n" +
                "  `id` bigint(20) NOT NULL COMMENT 'ID',\n" +
                "  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '资源编码\\n规则：\\n链接：\\n数据列：\\n按钮：',\n" +
                "  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '名称',\n" +
                "  `menu_id` bigint(20) NULL DEFAULT NULL COMMENT '菜单ID\\n#c_auth_menu',\n" +
                "  `describe_` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '描述',\n" +
                "  `create_user` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',\n" +
                "  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',\n" +
                "  `update_user` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',\n" +
                "  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',\n" +
                "  PRIMARY KEY (`id`) USING BTREE,\n" +
                "  UNIQUE INDEX `UN_CODE`(`code`) USING BTREE COMMENT '编码唯一'\n" +
                ") ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '资源' ROW_FORMAT = Dynamic;";

        sql = "SELECT\n" +
                "\tcount( 0 ) \n" +
                "FROM\n" +
                "\t(\n" +
                "SELECT\n" +
                "\tid,\n" +
                "\tNO,\n" +
                "\tNAME,\n" +
                "\taddress,\n" +
                "\taddressdetail,\n" +
                "IF\n" +
                "\t( longitude IS NULL, \"\", longitude ) AS longitude,\n" +
                "IF\n" +
                "\t( latitude IS NULL, \"\", latitude ) AS latitude,\n" +
                "\temail,\n" +
                "\tcontact,\n" +
                "\tcooperatestatus_id,\n" +
                "\tcontractstatus_id,\n" +
                "\tintro,\n" +
                "\tlogo,\n" +
                "\tservicescore,\n" +
                "\trecommen,\n" +
                "\ttax,\n" +
                "\tcreated_by,\n" +
                "\tupdated_by,\n" +
                "\tDATE_FORMAT( created_at, '%Y-%m-%d %h:%i:%s' ) AS created_at,\n" +
                "\tDATE_FORMAT( updated_at, '%Y-%m-%d %h:%i:%s' ) AS updated_at,\n" +
                "\tdeleted_at \n" +
                "FROM\n" +
                "\t`suppliers` \n" +
                "WHERE\n" +
                "\t1 = 1 \n" +
                "\tAND ( `no` LIKE CONCAT( '%',?, '%' ) OR `name` LIKE CONCAT( '%',?, '%' ) OR `intro` LIKE CONCAT( '%',?, '%' ) ) \n" +
                "\tAND `suppliers`.`deleted_at` IS NULL \n" +
                "ORDER BY\n" +
                "\t`id` DESC \n" +
                "\t) tmp_count";
//        TableNameParser tableNameParser = new TableNameParser(sql);
//        tableNameParser.tables().forEach(System.out::println);

        //存储过程
//        sql = "call insert_user(1, 2)";
//        // 函数
//        sql = "select get_base(2,1)";


//        sql = "SELECT count(0) FROM (SELECT `workorders`.`description`, `wss`.`name` AS `status_name`, `wos`.`place_id`, " +
//                "getPlaceFullName(wos.place_id, workorders.id) AS place_name, `workorders`.`money`, `workorders`.`id`, " +
//                "`workorders`.`title` AS `workordername`, `workorders`.`workorderid`, `workorders`.`updated_at`, " +
//                "`workorders`.`status`, `workorders`.`priority`, `workorders`.`wotype_id`, `workorders`.`title`" +
//                " FROM `workorders`" +
//                " LEFT JOIN `supplierservices` AS `s` ON `s`.`id` = `workorders`.`b_service_id`" +
//                " LEFT JOIN `wo_site` AS `wos` ON `workorders`.`id` = `wos`.`wo_id`" +
//                " LEFT JOIN `wo_status` AS `wss` ON `wss`.`id` = `workorders`.`status` " +
//                "WHERE `b_branch_id` = 43 AND `b_branch_id` = ? GROUP BY `workorders`.`id`) table_count";

        sql = "select * from c_auth_role_authority a\n" +
                "        where\n" +
                "        if(\n" +
                "        a.authority_type = 'RESOURCE',\n" +
                "        a.authority_id in (select id from c_auth_resource where use_type = 1),\n" +
                "        a.authority_id in (select id from c_auth_menu where use_type = 1)\n" +
                "        )\n" +
                "        and a.role_id = 1\n" +
                "        order by sort_value asc";

        System.out.println(ReplaceSql.replaceSql("1234", sql));
    }
}
