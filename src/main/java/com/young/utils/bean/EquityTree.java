package com.young.utils.bean;

import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Data
public class EquityTree {

    private static Map<String, Set<Equity>> map = new HashMap<>();

    private static Map<String, EquityTree> nodeMap = new HashMap<>();

    private static Set<String> idSet = new HashSet<>();

    private static String endName = "江苏亚虹医药科技股份有限公司";

    private static String filePath = "C:\\Users\\Administrator\\Documents\\WeChat Files\\lllwyii888\\FileStorage\\File\\2021-09\\江苏亚虹医药科技股份有限公司-股权穿透分析报告（投行版）-20210916013722 (2).xlsx";

    private static String targetName = "余治华";

    private static String outPath = "C:\\Users\\Administrator\\Desktop\\result.txt";


    /**
     * 节点
     */
    private Equity equity;

    /**
     * 有出资的
     */
    private Set<EquityTree> childes;

    public EquityTree(Equity equity) {
        this.equity = equity;
        this.childes = new HashSet<>();
    }

    public EquityTree(String filePath, String rootName) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(filePath);
        XSSFSheet sheet = workbook.getSheet("股权穿透结构");
        int lastRowNum = sheet.getLastRowNum();
        for (int i = 2; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            int lastColNum = row.getLastCellNum();
            for (int j = 0; j <= lastColNum; j++) {
                Cell czf = row.getCell(++j);
                if (czf != null) {
                    Cell btzf = row.getCell(++j);
                    Cell bl = row.getCell(++j);
                    Equity equity = new Equity();
                    String name = czf.getStringCellValue();
                    equity.setName(name);
                    equity.setCzdx(btzf.getStringCellValue());
                    equity.setCzbl(bl.getStringCellValue());
                    map.computeIfAbsent(name, K -> new HashSet<>()).add(equity);
                } else {
                    j += 2;
                }
            }
        }

        //根据map构建树
        Equity equity = new Equity();
        equity.setCzdx(rootName);
        this.equity = equity;
        this.childes = new HashSet<>();
        LinkedList<EquityTree> list = new LinkedList();
        list.add(this);
        while (!list.isEmpty()) {
            EquityTree node = list.removeLast();
            String lastName = node.getEquity().getCzdx();
            Set<EquityTree> cs = node.getChildes();
            if (!endName.equals(lastName) && map.containsKey(lastName)) {
                Set<Equity> set = map.get(lastName);
                for (Equity child : set) {
                    String cName = child.getCzdx();
                    String id = lastName + ":" + cName;
//                    if (!idSet.contains(id)) {
                        EquityTree equityTree = new EquityTree(child);
                        cs.add(equityTree);
//                        idSet.add(id);
                        list.add(equityTree);
//                    }
                }
            }
        }
    }


//    private void buildChild(EquityTree root, String name) {
//        if ("万得信息技术股份有限公司".equals(name) || !map.containsKey(name)) {
//            return;
//        }
//        Set<EquityTree> cs = root.getChildes();
//        Set<Equity> set = map.get(name);
//        for (Equity child : set) {
//            String cName = child.getCzdx();
//            String id = name + ":" + cName;
//            if (!idSet.contains(cName)) {
//                EquityTree tree = new EquityTree(child);
//                cs.add(tree);
//                idSet.add(id);
//                buildChild(tree, cName);
//            }
//        }
//    }


    public static void main(String[] args) throws IOException {
        EquityTree equityTree = new EquityTree(filePath, targetName);
        EquityTree.show(equityTree);
    }

    public static void show(EquityTree equityTree) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(outPath);
        f(pw, equityTree, new StringBuilder());
        pw.close();
    }

    private static void f(PrintWriter pw, EquityTree equityTree, StringBuilder sb) {
        Equity equity = equityTree.equity;
        sb.append(equity.getCzdx());
        if (equityTree.childes.size() == 0) {
            pw.println(sb.toString());
            pw.println();
            pw.flush();
            return;
        }
        sb.append("(");
        for (EquityTree sub : equityTree.childes) {
            f(pw, sub, new StringBuilder(sb).append(sub.equity.getCzbl()).append(") -> "));
        }
    }
}
