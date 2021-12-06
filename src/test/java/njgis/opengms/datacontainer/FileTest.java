package njgis.opengms.datacontainer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.datacontainer.service.DataContainer;
import njgis.opengms.datacontainer.utils.FileUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author mingyuan
 * @Date 2020.07.24 15:34
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FileTest {
    @Autowired
    DataContainer dataContainer;

    @Test
    public void writeFile() throws IOException, InterruptedException {
        System.out.println(FileUtil.currentWorkDir);

        StringBuilder sb = new StringBuilder();

        long originFileSize = 1024 * 1024 * 7;// 100M
        int blockFileSize = 1024 * 1024 * 1;// 15M

        // 生成一个大文件
        for (int i = 0; i < originFileSize; i++) {
            sb.append("A");
        }

        String fileName = FileUtil.currentWorkDir + "origin.myfile";
        System.out.println(fileName);
        System.out.println(FileUtil.write(fileName, sb.toString()));

        // 追加内容
        sb.setLength(0);
        sb.append("0123456789");
        FileUtil.append(fileName, sb.toString());

        FileUtil fileUtil = new FileUtil();

        // 将origin.myfile拆分
        dataContainer.splitBySize(fileName, blockFileSize);

        Thread.sleep(10000);// 稍等10秒，等前面的小文件全都写完

        // 合并成新文件
//        dataContainer.mergePartFiles(FileUtil.currentWorkDir, ".part",
//                blockFileSize, FileUtil.currentWorkDir + "new.myfile");
    }

    @Test
    public void test()throws IOException, InterruptedException {
        Map<String, Object> param = new HashMap<String, Object>();
        String url = "http://111.229.14.128:8898/findData";
        String token = "uGi4gMg94+ux4nuugF0M9tlqpCtZFRqem1kl/J2Vra8=";
        param.put("token", token);
        param.put("modelName", "instances");
        JSONObject searchCont = new JSONObject();
        searchCont.put("type", "Processing");
        searchCont.put("name", "Rainfall_Category_Curve");
        param.put("searchCont", searchCont);
        //创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<JSONObject> entity = new HttpEntity(param, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        res.getBody();// finddata
    }
}
