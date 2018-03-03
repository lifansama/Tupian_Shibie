package com.example;

/**
 * Created by 李旭 on 2018/2/28  15:11
 */

public class cuowu_dm {

    public String getString(int cuowu){
        String dm = "错误："+cuowu;
        switch (cuowu){
            case 1:
            case 2:
                dm = "服务器内部错误，请再次请求";
                break;
            case 3:
                dm = "调用的API不存在，请检查后重新尝试";
                break;
            case 4:
                dm = "集群超限额";
                break;
            case 5:
                dm = "无权限访问该用户数据";
                break;
            case 17:
                dm = "每天请求量超限额";
                break;
            case 18:
                dm = "QPS超限额,请重试";
                break;
            case 19:
                dm = "请求总量超限额";
                break;
            case 100:
                dm = "无效的access_token参数，请检查后重新尝试";
                break;
            case 110:
            case 111:
                dm = "Token过期失效,请重新启动";
                break;
            case 282000:
                dm = "服务器内部错误，如果您使用的是高精度接口，报这个错误码的原因可能是您上传的图片中文字过多，识别超时导致的，建议您对图片进行切割后再识别";
                break;
            case 216200:
                dm = "图片为空，请检查后重新尝试";
                break;
            case 216202:
                dm = "图片尺寸不对，请重新选择";
                break;
            case 283504:
                dm = "网络请求失败";
                break;
            case 283505:
                dm = "服务器返回数据异常";
                break;
            case 283601:
                dm = "身份验证错误";
                break;
            case 283602:
                dm = "请确保不要改变调用设备的本地时间!";
                break;
            case 283501:
            case 283604:
            case 283502:
                dm = "请在控制台中配置正确的包名，并确认使用了正确的授权文件";
                break;
            case 283700:
                dm = "服务器内部错误";
                break;
        }
        return dm;
    }

}
