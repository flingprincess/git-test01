package com.charon.qiniuyun;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.charon.util.FileUtil;
import com.charon.util.HttpClientUtils;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.UnsupportedEncodingException;

@SpringBootTest
class QiniuyunApplicationTests {

    @Test
    void contextLoads() {
    }
    @Test
    void hello(){
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.huadongZheJiang2());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
//...其他参数参考类注释

        UploadManager uploadManager = new UploadManager(cfg);
//...生成上传凭证，然后准备上传
        String accessKey = "4EPoJOJqzZtbfqjLvazri3jVNkf47fi5A-45Twp-";
        String secretKey = "C42YzsW6GEqbAfOZNkj52ejgZr02DXSoB6FyZCmX";
        String bucket = "kaoheduixianhgcuichu";

//默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = "x.png";
        try {
            byte[] uploadBytes = FileUtil.getBytesByFile("C:\\Users\\86136\\Pictures\\Snipaste_2022-06-17_13-03-30.png");
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            try {
                Response response = uploadManager.put(uploadBytes, key, upToken);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                System.out.println("http://rx9f7ozfc.bkt.clouddn.com/"+putRet.key);
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception ex) {
            //ignore
        }


    }
    @Test
    void weatherInfo(){
        /*String lat = "101.110880";
        String lon = "31.874000";
        String location = lat+","+lon;*/
        String location = "39.99281,116.31088";
        String baiduAK = "PpMp6FKxA3uGaH5eLHDurHkT0IIIxyXR";
        String url = "https://api.map.baidu.com/reverse_geocoding/v3/?ak="+baiduAK+"&output=json&coordtype=wgs84ll&location="+location;
        try {
            String s = HttpClientUtils.sendGet(url);
            System.out.println(s);
            JSONObject city = JSON.parseObject(s);
            JSONObject result = (JSONObject) city.get("result");
            JSONObject addressComponent = (JSONObject) result.get("addressComponent");
            String adcode = addressComponent.get("adcode").toString();
            System.out.println();
            System.out.println(HttpClientUtils.sendGet("https://restapi.amap.com/v3/weather/weatherInfo?key=218e457c979b863c472f428989f4fd95&city="+adcode+"&extensions=all&output=json"));
           /* JSONObject object = JSONObject.parseObject(s);
            String status = object.get("status").toString();
            if (status.equals("0")){
                JSONObject result = (JSONObject) object.get("result");
                JSONObject addressComponent = (JSONObject) result.get("addressComponent");
                String city = (String) addressComponent.get("city");
                System.out.println(city);;
            }*/
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
