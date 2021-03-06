import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Parser {

    // 指定加载文档的路径
    private static final String INPUT_FILE ="C:\\Users\\rain7\\Desktop\\docs";

    public void run(){
        ArrayList<File> fileList = new ArrayList<File>();

        // 遍历文档路径,获取文档中所有的 HTML 文件
        File rootFile = new File(INPUT_FILE);
        emnuFile(rootFile,fileList);

        // 对 每个html文件进行内容解析
        for (File file:fileList) {
            System.out.println("开始解析： " +file.getName());

            parseHTML(file);
        }

    }

    /**
     * 解析 HTML 文件
     * @param file
     * @return
     */
    private String[] parseHTML(File file) {
        // 解析html文件需要 解析正文 以及 标题、描述（正文的一段摘要）、url获取到
        // 要想得到描述必须拿到正文

        //1、解析HTML的标题
        String title = parseTitle(file);

        //2、解析HTML对应的URL
        String url = parseUrl(file);

        //3、解析出HTML 对应的正文
        String content = parseContent(file);

        //4、拿到描述
        return null;
    }

    /**
     * 解析html文件的正文,读取<div><div/>中包括的内容
     * @return
     */
    private  String parseContent(File file) {
        try(FileReader fileReader = new FileReader(file)) {
            // 是否要拷贝的开关
            boolean isCopy = true;
            StringBuilder content = new StringBuilder();
            while(true){
                int ret =  fileReader.read();
                if(ret==-1){
                    break;
                }
                //如果不是-1，那么就是一个合法的字符
                char c = (char)ret;
                // 对字符进行识别，判断拷贝开关是否开启关闭
                if(isCopy){ // 如果拷贝开关为true，进入条件中
                    if(c=='<'){//如果碰到<,那么关闭开关
                        isCopy=false;
                        continue;
                    }
                    if(c=='\r' || c=='\n'){// 经过测试查看，发现原文中有很多换行符，所以去除，方便后续截取摘要
                        c=' ';
                    }
                    // 如果不是左括号，同时开关是开的，那么拷贝字符
                    content.append(c);
                }else{ //如果拷贝开关为false,那么跳过不拷贝
                    //如果字符为>,那么拷贝开关打开
                    if(c=='>'){
                        isCopy=true;
                    }
                }
            }
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 解析html文件的url
     * @return
     */
    private String parseUrl(File file) {
        // 这里展示的url，我们希望能够跳转到线上java文档的地址
        // 所以展示的是 java线上文档的url

        // 线上文档的url   https://docs.oracle.com/javase/8/docs/api/
        // 本地文档的url        C:\Users\rain7\Desktop\docs\api\java\awt\color\CMMException.html

        // 线上文档的前半部分 ulr
        String part1 ="https://docs.oracle.com/javase/8/docs";

        // 截取本地文档中 除前半部分的固定url 的后半内容
        String part2 = file.getAbsolutePath().substring(INPUT_FILE.length());
        // part2中的反斜杠全部替换成正斜杠，其实浏览器自身的容错能力也支持 反斜杠、正斜杠识别
        part2 = part2.replaceAll("\\\\","/");

        return part1+part2;
    }

    public static void main(String[] args) {
        Parser parser = new Parser();
        File file = new File("C:\\Users\\rain7\\Desktop\\docs\\api\\java\\lang\\class-use\\Appendable.html");
        System.out.println(parser.parseUrl(file));
    }

    /**
     * 解析html文件的标题
     * @return 返回搜索结果的标题
     */
    private String parseTitle(File file) {
        // 通过查看html文件的源码，发现title 标签的内容就是文件名

        int index = file.getName().lastIndexOf('.');
        return file.getName().substring(0,index);
    }



    /**
     * 遍历根目录文件下的所有非 HTML文件，保存文件到 list集合中
     * @param rootFile 传入的根目录文件，
     * @param fileList 符合条件的文件保存到该集合中
     */

    private void  emnuFile(File rootFile, ArrayList<File> fileList) {
        // 要通过递归操作，遍历跟路径中的所有普通文件
        File[] files = rootFile.listFiles(); // 显示当前文件对象下的所有文件，整合成文件对象放到数组中，只显示一级

        for (File file:files) {
            if(file.isDirectory()){ // 如果文件是目录的话，继续递归下去
                emnuFile(file,fileList);
            }else{ // 如果不是目录，只是普通的文件

                // 排除枚举的所有文件中的 非HTML文件
                if(file.getAbsolutePath().endsWith(".html")){
                    fileList.add(file);
                }
            }
        }
    }


}
