package com.xq.secser.secser.service;

import com.xq.secser.secser.model.FundTypeEnum;
import com.xq.secser.secser.pojo.po.CompPo;
import com.xq.secser.secser.utils.FileWirter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
@Service
public class ComService {
    private static Logger logger = LoggerFactory.getLogger(ComService.class);

    //todo:有没有法根据平台设置不同的值，而不是不同的bean
    @Value("${com.xq.secser.download.path}")
    private String fileApath;
    private static RestTemplate restTemplate = new RestTemplate();

    public void getAllCom(FundTypeEnum ft) {
        String pattern = "http://fund.eastmoney.com/Company/home/gspmlist?fundType=%d";
        String url = String.format(pattern, ft.getIcode());

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String data = responseEntity.getBody();
        data = data.replace("&nbsp;", "");

        FileWirter.writeFile(getFilePath(ft), data);
    }

    public String getFilePath(FundTypeEnum ft) {
        String fileFullName = fileApath + File.separator + ft.getUrlParam() + ".xml";
        logger.info("file path={}", fileFullName);
        return fileFullName;
    }

    public List<CompPo> parseCompFile(FundTypeEnum ft) {
        List<CompPo> compPoList = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(new File(getFilePath(ft)));
            Element root = document.getDocumentElement();
            // 获取孩子元素
            NodeList childNodes = root.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node instanceof Element) {
                    if ("tbody".equals(node.getNodeName())) {
                        NodeList theadChildList = node.getChildNodes();
                        for (int j = 0; j < theadChildList.getLength(); ++j) {
                            Node thSubNode = theadChildList.item(j);
                            if ("tr".equals(thSubNode.getNodeName())) {
                                CompPo comp = CompPo.builder().build();
                                NodeList itemNameList = thSubNode.getChildNodes();
                                for (int k = 0; k < itemNameList.getLength(); ++k) {
                                    Node itemNode = itemNameList.item(k);
                                    if ("td".equals(itemNode.getNodeName())) {
                                        String content = itemNode.getTextContent().replaceAll("\\s*", "");
                                        switch (k) {
                                            //index
                                            case 1:
                                                comp.setIndex(Long.valueOf(content));
                                                break;
                                            //name
                                            case 3:
                                                comp.setName(content);
                                                break;
                                            //es time
                                            case 7:
                                                comp.setEstime(content);
                                                break;
                                            //scale
                                            case 11:
                                                if (!"-".equals(content)) {
                                                    comp.setScale(Double.valueOf(content.substring(0, content.length() - 5).replace(",", "")));
                                                }
                                                break;
                                            //found number
                                            case 13:
                                                comp.setFnnum(Long.valueOf(content));
                                                break;
                                            //manger number
                                            case 15:
                                                comp.setManagernum(Long.valueOf(content));
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                                comp.setFt(ft.getUrlParam());
                                compPoList.add(comp);
                            }
                        }
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        compPoList.forEach(info -> System.out.println(String.format("%d %s %s %s %f %d %d", info.getIndex(), info.getName(), info.getEstime(), info.getFt(), info.getScale(), info.getFnnum(), info.getManagernum())));

        return compPoList;
    }
}
