package com.xq.secser.ssbser.tt.provider;

import com.xq.secser.ssbser.pojo.po.FoundLevelPo;
import com.xq.secser.ssbser.service.FundLevelProvider;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2020/1/2
 */
@Service
public class TTFundLevelProvider implements FundLevelProvider {
    private static Logger logger = LoggerFactory.getLogger(TTFundLevelProvider.class);

    @Override
    public void initFundLevelData() {
        //表格从http://fund.eastmoney.com/data/fundrating.html 基金评级得到
    }

    @Override
    public List<FoundLevelPo> parseFundLevelData() {
        List<FoundLevelPo> foundLevelPoList = new ArrayList<>();
        try {
            Resource res2 = new ClassPathResource("评级.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(res2.getInputStream());
            XSSFSheet dataSheet = workbook.getSheet("data");
            XSSFRow dataheader = dataSheet == null ? null : dataSheet.getRow(0);
            int itfCellNum = dataheader == null ? 0 : dataheader.getPhysicalNumberOfCells();
            int fcodei = 0;
            int ccodei = 0;
            int flevelsh = -1, flevelzs = -1, flevelja = -1;
            for (int i = 0; i < itfCellNum; i++) {
                String content = getCellString(dataheader, i);
                switch (content) {
                    case "代码":
                        fcodei = i;
                        break;
                    case "简称":
                        break;
                    case "相关链接":
                        break;
                    case "基金经理":
                        break;
                    case "基金公司":
                        ccodei = i;
                        break;
                    case "5星评级家数":
                        break;
                    case "上海证券":
                        flevelsh = i;
                        break;
                    case "招商证券":
                        flevelzs = i;
                        break;
                    case "济安金信":
                        flevelja = i;
                        break;
                    default:
                        break;
                }
            }

            int itfRowsNum = dataSheet.getPhysicalNumberOfRows();
            for (int i = 1; i < itfRowsNum; i++) {
                FoundLevelPo foundLevelPo = FoundLevelPo.builder().build();
                //从第二列获取
                XSSFRow xssfRow = dataSheet.getRow(i);
                String code = getCellString(xssfRow, fcodei);
                while (code.length() < 6) {
                    code = "0" + code;
                }
                foundLevelPo.setCode(code);

                String ccode = getLinkString(xssfRow, ccodei);
                ccode = ccode.replaceAll("[^\\d]+", "");
                foundLevelPo.setComcode(ccode);

                String lsh = getCellString(xssfRow, flevelsh);
                lsh = lsh.replaceAll("[^★]+", "");

                String lzs = getCellString(xssfRow, flevelzs);
                lzs = lzs.replaceAll("[^★]+", "");

                String lja = getCellString(xssfRow, flevelja);
                lja = lja.replaceAll("[^★]+", "");
                double level = getLevel(lsh.length(), lzs.length(), lja.length());
                foundLevelPo.setLevel(level);
                foundLevelPoList.add(foundLevelPo);
                logger.debug("{}-{}-{}-{}-{}==={}", code, ccode, lsh, lzs, lja, level);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return foundLevelPoList;
    }

    public double getLevel(double i, double j, double k) {
        double sum = 0;
        int num = 0;
        if (i > 0) {
            sum += i;
            num++;
        }
        if (j > 0) {
            sum += j;
            num++;
        }
        if (k > 0) {
            sum += k;
            num++;
        }

        return num > 0 ? (sum / num) : 0d;
    }

    public static String getCellString(XSSFRow xssfRow, int index) {
        XSSFCell cell = xssfRow.getCell(index);
        if (cell == null) {
            return "";
        }
        cell.setCellType(CellType.STRING);
        return cell.toString();
    }

    public static String getLinkString(XSSFRow xssfRow, int index) {
        XSSFCell cell = xssfRow.getCell(index);
        if (cell == null) {
            return "";
        }
        XSSFHyperlink link = cell.getHyperlink();
        if (link == null) {
            return "";
        }
        return link.getAddress();
    }
}
