package com.mistywillow.researchdb;

import org.apache.commons.text.StringEscapeUtils;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReadXMLFileDOMParser {

    public List<HashMap<String, List<String>>> getImportNotes() {
        return importNotes;
    }
    private List<HashMap<String, List<String>>> importNotes = new ArrayList<>();

    public ReadXMLFileDOMParser(String fileName) {

        try {
            InputStream istream = new FileInputStream(fileName);
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(istream);
            NodeList noteList = doc.getElementsByTagName("note");

            for (int n = 0; n < noteList.getLength(); n++) {
                NodeList tableList = noteList.item(n).getChildNodes();
                HashMap<String, List<String>> tables = new HashMap<>();
                doc.getDocumentElement().normalize();

                for (int t = 0; t < tableList.getLength(); t++) {

                    Node node = tableList.item(t);
                    tableList.item(t).getNodeName();


                    Element tElem = (Element) node;

                    NodeList child = tElem.getChildNodes();
                    List<String> fields = new ArrayList<>();
                    for (int c = 0; c < child.getLength(); c++) {
                        if (!child.item(c).getNodeName().equals("author" + (c + 1)) && !child.item(c).getNodeName().equals("file" + (c + 1))) {
                            fields.add(StringEscapeUtils.unescapeXml(child.item(c).getTextContent()));
                        } else {
                            NodeList childList = child.item(c).getChildNodes();
                            StringBuilder sb = new StringBuilder();
                            for (int cl = 0; cl < childList.getLength(); cl++) {
                                if (cl < (childList.getLength() - 1)) {
                                    sb.append(childList.item(cl).getTextContent()).append("*");
                                } else {
                                    fields.add(sb.append(childList.item(cl).getTextContent()).toString());
                                }
                            }
                        }
                    }
                    tables.put(tElem.getAttribute("name"), fields);

                }
                importNotes.add(tables);
            }
        } catch (
                IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }
}
