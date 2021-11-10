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
public class EquityTree2 {

    private static Map<String, Set<Equity>> map = new HashMap<>();

    private static Map<String, EquityTree2> nodeMap = new HashMap<>();

    private static Set<String> idSet = new HashSet<>();

    private static Set<String> nameSet = new HashSet<>();

    private static String endName = "万得信息技术股份有限公司";

    private static String filePath = "C:\\Users\\Administrator\\Documents\\WeChat Files\\lllwyii888\\FileStorage\\File\\2021-09\\江苏亚虹医药科技股份有限公司-股权穿透分析报告（投行版）-20210916013722 (2).xlsx";

    private static String targetName = "昆吾九鼎投资控股股份有限公司";

    private static String outPath = "C:\\Users\\Administrator\\Desktop\\result.txt";

    private static Map<String,List<String>> outLineMap = new HashMap<>();

    private static List<String> endLineList = new ArrayList<>();


    /**
     * 节点
     */
    private Equity equity;

    /**
     * 有出资的
     */
    private Set<EquityTree2> childes;

    public EquityTree2(Equity equity) {
        this.equity = equity;
        this.childes = new HashSet<>();
    }

    public EquityTree2(String filePath, String rootName) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(filePath);
        XSSFSheet sheet2 = workbook.getSheet("穿透主体汇总");
        int lastRowNum = sheet2.getLastRowNum();
        for (int i = 1;i<=lastRowNum;i++){
            Row row = sheet2.getRow(i);
            outLineMap.computeIfAbsent(row.getCell(0).getStringCellValue(), K -> new ArrayList<>()).add(row.getCell(8).getStringCellValue());
        }

        XSSFSheet sheet = workbook.getSheet("股权穿透结构");
        lastRowNum = sheet.getLastRowNum();
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
        LinkedList<EquityTree2> list = new LinkedList();
        list.add(this);
        while (!list.isEmpty()) {
            EquityTree2 node = list.removeLast();
            String lastName = node.getEquity().getCzdx();
            Set<EquityTree2> cs = node.getChildes();
            if (!endName.equals(lastName) && map.containsKey(lastName)) {
                Set<Equity> set = map.get(lastName);
                for (Equity child : set) {
                    String cName = child.getCzdx();
                    String id = lastName + ":" + cName;
                    if (!idSet.contains(id)) {
                        EquityTree2 equityTree = new EquityTree2(child);
                        cs.add(equityTree);
                        idSet.add(id);
                        list.add(equityTree);
                    }
                }
            }
        }
    }


    public static void main(String[] args) throws IOException {
        EquityTree2 equityTree = new EquityTree2(filePath, targetName);
        EquityTree2.show(equityTree);
    }

    public static void show(EquityTree2 equityTree) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(outPath);
        f(pw, equityTree, new StringBuilder(),new HashSet<>());
        pw.println(count);
        pw.close();
    }

    private static int count = 0;
    private static void f(PrintWriter pw, EquityTree2 equityTree, StringBuilder sb,Set<String> set) {
        Equity equity = equityTree.equity;
        String czdx = equity.getCzdx();
        if(set.contains(czdx)){
            return;
        }
        if (equityTree.childes.size() == 0) {
            if(!endName.equals(czdx) && outLineMap.containsKey(czdx)){
                outLineMap.get(czdx);
                List<String> list = outLineMap.get(czdx);
                out:
                for(String s:list){
                    for (String com:set){
                        if(s.indexOf(com) > -1){
                            continue out;
                        }
                    }
                    count++;
                    System.out.println(sb.toString());
                    System.out.println(s);
                    pw.println(sb.toString() + s);
                    pw.println();
                    pw.flush();
                }
            }else {
                sb.append(czdx);
                count++;
                pw.println(sb.toString());
                pw.println();
                pw.flush();
            }
            return;
        }
        set.add(czdx);
        sb.append(czdx);
        sb.append("(");
        for (EquityTree2 sub : equityTree.childes) {
            f(pw, sub, new StringBuilder(sb).append(sub.equity.getCzbl()).append(") -> "),new HashSet<>(set));
        }
    }
}
