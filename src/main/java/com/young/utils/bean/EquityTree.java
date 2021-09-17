package com.young.utils.bean;

import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class EquityTree {

    private Map<String, Set<Equity>> map = new HashMap<>();

    /**
     * 节点
     */
    private Equity equity;

    /**
     * 有出资的
     */
    private Set<EquityTree> child;

    public EquityTree(String rootName){
        Equity equity = new Equity();
        equity.setName(rootName);
        this.equity = equity;
    }

    public EquityTree(String filePath, String rootName) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(filePath);
        XSSFSheet sheet = workbook.getSheet("股权穿透结构");
        int lastRowNum = sheet.getLastRowNum();
        for (int i = 2; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < 200; j++) {
                Cell czf = row.getCell(++j);
                if (czf != null) {
                    Cell btzf = row.getCell(++j);
                    Cell bl = row.getCell(++j);
                    Equity equity = new Equity();
                    String name = czf.getStringCellValue();
                    equity.setName(name);
                    equity.setCzdx(btzf.getStringCellValue());
                    equity.setCzbl(bl.getStringCellValue());
                    map.computeIfAbsent(name,K -> new HashSet<>()).add(equity);
                } else {
                    j += 2;
                }
            }
        }

        //根据map构建树
        Equity equity = new Equity();
        equity.setName(rootName);
        this.equity = equity;

    }

    public static void main(String[] args) throws IOException {
        EquityTree equityTree = new EquityTree("C:\\Users\\Administrator\\Documents\\WeChat Files\\lllwyii888\\FileStorage\\File\\2021-09\\【完整】江苏亚虹医药科技股份有限公司-股权穿透分析报告（投行版）-20210916013722.xlsx", "中国中信有限公司");

    }
}
