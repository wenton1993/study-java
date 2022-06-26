import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class XmlAppender {

    /**
     * 添加 XML 到 DataJava
     *
     * <br>输入：DataJava
     *
     * <br>步骤：
     * <ol>
     *   <li>按行读取 java 文件</li>
     *   <li>通过正则表达式找：private List<String> contractReviewId;</li>
     *   <li>在属性定义前添加：@XmlElement(name = "contractItemNo")</li>
     *   <li>收集所有属性，打印 "property1", "property2", "property3"...，添加到 @XmlType(propOrder = {...})</li>
     * </ol>
     */
    @Test
    public void appendXml() throws IOException {
        Path file = Paths.get("D:\\Users\\00768976\\IdeaProjectsC\\bmp-iis-ws\\bmp-iis-ws-srv\\src\\main\\java\\com\\zvos\\bmp\\iis\\ws\\application\\dto\\mes\\QuerySalesOrderListByMesNoSessionReqBodyData.java");

        // 文件名：Employee.java
        Path fileName = file.getFileName();
        // 类名：Employee
        String className = fileName.toString().replaceAll("\\..*", "");

        // 新类名
        String newClassName = className + 2;
        // 新文件名
        String newFileName = newClassName + ".java";

        Path newFile = file.getParent().resolve(newFileName);

        // @XmlType(propOrder = {...})
        StringJoiner propOrder = new StringJoiner(", ");

        try (BufferedReader reader = Files.newBufferedReader(file);
             BufferedWriter writer = Files.newBufferedWriter(newFile)) {
            // 匹配：private List<String> contractReviewId;
            String regex = "private\\s+[\\w<>]+\\s+([\\w]+);";
            Pattern p = Pattern.compile(regex);

            String line = null;
            while (Objects.nonNull(line = reader.readLine())) {
                // 替换类名，方便使用 alt + F6
                line.replaceAll(className, newClassName);

                // 添加 @XmlElement
                Matcher m = p.matcher(line);
                if (m.find()) {
                    String prop = m.group(1);
                    line = "    @XmlElement(name = \"" + prop + "\")\n" + line;

                    propOrder.add("\"" + prop + "\"");
                }

                // 避免添加多余的换行
                if (!line.matches("\n$")) {
                    line = line + "\n";
                }

                writer.write(line);
            }
        }

        log.info(propOrder.toString());
    }
}
