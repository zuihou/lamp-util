package top.tangyh.basic.utils;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.parser.NodeParser;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import top.tangyh.basic.base.entity.TreeEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * list列表 转换成tree列表
 * Created by Ace on 2017/6/12.
 *
 * @author zuihou
 */
public final class TreeUtil {
    /**
     * 默认的树节点 分隔符
     */
    public static final String TREE_SPLIT = StrPool.SLASH;
    /**
     * 默认的父id
     */
    public static final Long DEF_PARENT_ID = 0L;
    private static final int TOP_LEVEL = 1;

    private TreeUtil() {
    }

    public static String getTreePath(String parentTreePath, Long parentId) {
        return parentTreePath + parentId + TREE_SPLIT;
    }

    public static String buildTreePath(Long id) {
        return TREE_SPLIT + id + TREE_SPLIT;
    }

    /**
     * 判断id是否为根节点
     *
     * @param id
     * @return
     */
    public static boolean isRoot(Long id) {
        return id == null || DEF_PARENT_ID.equals(id);
    }

    /**
     * 构建Tree结构
     *
     * @param treeList 待转换的集合
     * @return 树结构
     */
    public static <E extends TreeEntity<E, ? extends Serializable>> List<E> buildTree(Collection<E> treeList) {
        if (CollUtil.isEmpty(treeList)) {
            return Collections.emptyList();
        }
        //记录自己是自己的父节点的id集合
        List<Serializable> selfIdEqSelfParent = new ArrayList<>();
        // 为每一个节点找到子节点集合
        for (E parent : treeList) {
            Serializable id = parent.getId();
            for (E children : treeList) {
                if (parent != children) {
                    //parent != children 这个来判断自己的孩子不允许是自己，因为有时候，根节点的parent会被设置成为自己
                    if (id.equals(children.getParentId())) {
                        parent.initChildren();
                        parent.getChildren().add(children);
                    }
                } else if (id.equals(parent.getParentId())) {
                    selfIdEqSelfParent.add(id);
                }
            }
        }
        // 找出根节点集合
        List<E> trees = new ArrayList<>();

        List<? extends Serializable> allIds = treeList.stream().map(TreeEntity::getId).toList();
        for (E baseNode : treeList) {
            if (!allIds.contains(baseNode.getParentId()) || selfIdEqSelfParent.contains(baseNode.getParentId())) {
                trees.add(baseNode);
            }
        }
        return trees;
    }

    public static Long getTopNodeId(String treePath) {
        String[] pathIds = StrUtil.splitToArray(treePath, TREE_SPLIT);
        if (ArrayUtil.isNotEmpty(pathIds)) {
            return Convert.toLong(pathIds[TOP_LEVEL]);
        }
        return null;
    }


    /**
     * 构建 根节点存储null，节点ID类型为Long 的树
     *
     * @param <T>            转换的实体 为数据源里的对象类型
     * @param list           源数据集合; 必须继承TreeNode<Long>
     * @return List
     */
    public static <T extends TreeNode<Long>> List<Tree<Long>> build(List<T> list) {
        return build(list, TreeNodeConfig.DEFAULT_CONFIG);
    }

    /**
     * 构建 根节点存储null，节点ID类型为Long 的树
     *
     * @param <T>            转换的实体 为数据源里的对象类型
     * @param list           源数据集合; 必须继承TreeNode<Long>
     * @param treeNodeConfig 配置
     * @return List
     */
    public static <T extends TreeNode<Long>> List<Tree<Long>> build(List<T> list, TreeNodeConfig treeNodeConfig) {
        return build(list, DEF_PARENT_ID, treeNodeConfig);
    }

    /**
     * 构建  根节点存储 <code>rootId</code>，节点ID类型为Long 的树
     *
     * @param <T>            转换的实体 为数据源里的对象类型
     * @param <E>            ID类型
     * @param list           源数据集合; 必须继承TreeNode<E>
     * @param rootId         最顶层父id值 一般为 0 或 null 之类
     * @return List
     */
    public static <T extends TreeNode<E>, E> List<Tree<E>> build(List<T> list, E rootId) {
        return build(list, rootId, TreeNodeConfig.DEFAULT_CONFIG);
    }

    /**
     * 构建  根节点存储 <code>rootId</code>，节点ID类型为Long 的树
     *
     * @param <T>            转换的实体 为数据源里的对象类型
     * @param <E>            ID类型
     * @param list           源数据集合; 必须继承TreeNode<E>
     * @param rootId         最顶层父id值 一般为 0 或 null 之类
     * @param treeNodeConfig 配置
     * @return List
     */
    public static <T extends TreeNode<E>, E> List<Tree<E>> build(List<T> list, E rootId, TreeNodeConfig treeNodeConfig) {
        return cn.hutool.core.lang.tree.TreeUtil.build(list, rootId, treeNodeConfig, new FsNodeParser<>());
    }


    /**
     * 构建  根节点存储 <code>rootId</code>，节点ID类型为Long 的树
     *
     * @param <T>            转换的实体 为数据源里的对象类型
     * @param list           源数据集合; 必须继承TreeNode<E>
     * @param nodeParser  解析器
     * @return List
     */
    public static <T extends TreeNode<Long>> List<Tree<Long>> build(List<T> list, NodeParser<T, Long> nodeParser) {
        return cn.hutool.core.lang.tree.TreeUtil.build(list, DEF_PARENT_ID, TreeNodeConfig.DEFAULT_CONFIG, nodeParser);
    }

    public static class FsNodeParser<T extends TreeNode<E>, E> implements NodeParser<T, E> {
        @Override
        public void parse(T treeNode, Tree<E> tree) {
            tree.setId(treeNode.getId());
            tree.setParentId(treeNode.getParentId());
            tree.setWeight(treeNode.getWeight());
            tree.setName(treeNode.getName());
            //扩展字段
            final Map<String, Object> extra = treeNode.getExtra();
            if (MapUtil.isNotEmpty(extra)) {
                extra.forEach(tree::putExtra);
            }
        }
    }

}
