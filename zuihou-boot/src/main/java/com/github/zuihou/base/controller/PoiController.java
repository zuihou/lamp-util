package com.github.zuihou.base.controller;

import cn.afterturn.easypoi.entity.vo.NormalExcelConstants;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.ExcelXorHtmlUtil;
import cn.afterturn.easypoi.excel.entity.ExcelToHtmlParams;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.view.PoiBaseView;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.zuihou.base.R;
import com.github.zuihou.base.request.PageParams;
import com.github.zuihou.log.annotation.SysLog;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 导入导出
 *
 * @param <Entity>  实体
 * @param <Id>      主键
 * @param <PageDTO> 分页查询参数
 * @author zuihou
 * @date 2020年03月07日22:02:06
 */
public interface PoiController<Entity, Id extends Serializable, PageDTO> extends QueryController<Entity, Id, PageDTO> {

    /**
     * 导出Excel
     *
     * @param params   参数
     * @param request  请求
     * @param response 响应
     * @throws IOException
     */
    @ApiOperation(value = "导出Excel")
    @RequestMapping(value = "/export", method = RequestMethod.POST, produces = "application/octet-stream")
    @SysLog("'导出Excel:'.concat(#params.map[" + NormalExcelConstants.FILE_NAME + "]?:'')")
    default void exportExcel(@RequestBody @Validated PageParams<PageDTO> params, HttpServletRequest request, HttpServletResponse response) {
        IPage<Entity> page = params.getPage();
        ExportParams exportParams = getExportParams(params, page);

        Map<String, Object> map = new HashMap<>(5);
        map.put(NormalExcelConstants.DATA_LIST, page.getRecords());
        map.put(NormalExcelConstants.CLASS, getEntityClass());
        map.put(NormalExcelConstants.PARAMS, exportParams);
        String fileName = params.getMap().getOrDefault(NormalExcelConstants.FILE_NAME, "临时文件");
        map.put(NormalExcelConstants.FILE_NAME, fileName);
        PoiBaseView.render(map, request, response, NormalExcelConstants.EASYPOI_EXCEL_VIEW);
    }

    /**
     * 预览Excel
     *
     * @param params 预览参数
     * @return
     */
    @ApiOperation(value = "预览Excel")
    @SysLog("'预览Excel:' + (#params.map[" + NormalExcelConstants.FILE_NAME + "]?:'')")
    @RequestMapping(value = "/preview", method = RequestMethod.POST)
    default R<String> preview(@RequestBody @Validated PageParams<PageDTO> params) {
        IPage<Entity> page = params.getPage();
        ExportParams exportParams = getExportParams(params, page);

        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, getEntityClass(), page.getRecords());
        return success(ExcelXorHtmlUtil.excelToHtml(new ExcelToHtmlParams(workbook)));
    }

    /**
     * 使用自动生成的实体+注解方式导入 对RemoteData 类型的字段不支持，
     * 建议自建实体使用
     *
     * @param simpleFile
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "导入Excel")
    @PostMapping(value = "/import")
    @SysLog(value = "'导入Excel:' + #simpleFile?.originalFilename", request = false)
    default R<Boolean> importExcel(@RequestParam(value = "file") MultipartFile simpleFile) throws Exception {
        ImportParams params = new ImportParams();
        params.setTitleRows(0);
        params.setHeadRows(1);
        List<Map<String, String>> list = ExcelImportUtil.importExcel(simpleFile.getInputStream(), Map.class, params);

        if (list != null && !list.isEmpty()) {
            handlerImport(list);
        }
        return success();
    }

    default ExportParams getExportParams(PageParams<PageDTO> params, IPage<Entity> page) {
        query(params, page, params.getSize() == -1 ? Convert.toLong(Integer.MAX_VALUE) : params.getSize());

        String title = params.getMap().get("title");
        String type = params.getMap().getOrDefault("type", ExcelType.XSSF.name());
        String sheetName = params.getMap().getOrDefault("sheetName", "SheetName");

        ExcelType excelType = ExcelType.XSSF.name().equals(type) ? ExcelType.XSSF : ExcelType.HSSF;
        return new ExportParams(title, sheetName, excelType);
    }
}
