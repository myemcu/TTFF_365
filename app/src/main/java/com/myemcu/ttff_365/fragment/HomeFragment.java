package com.myemcu.ttff_365.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.myemcu.ttff_365.R;
import com.myemcu.ttff_365.activity.WebDetailActivity;
import com.myemcu.ttff_365.adapter.HomeListViewAdapter;
import com.myemcu.ttff_365.javabean.HomeDataResult;
import com.myemcu.ttff_365.ui.ImplantListView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/11/17 0017.
 */

// 继承Fragment，必须实现onCreateView()方法
// Fragment生命周期中先是调用onCreateView()，再调用onActivityCreated()

// 用于PC看网址的(http://v2.ffu365.com/index.php?m=Api&c=Index&a=home&appid=1)

public class HomeFragment extends Fragment {

    private ImageView iv_top_photo,iv_recommend_compary;

    //private ListView list_view;
    private ImplantListView list_view;  // 使用自定义的ListView

    private View view;

    private Handler handler = new Handler();
    private HomeDataResult homeDataResult;  // bean对象

    private boolean isVisable = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        //判断Fragment中的ListView时候存在，判断该Fragment时候已经正在前台显示  通过这两个判断，就可以知道什么时候去加载数据了
        if (getUserVisibleHint() && isVisible()) {
            isVisable = true;
        }else{
            isVisable = false;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Nullable
    @Override   // Fragment视图
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home,null);
        return view;
    }

    @Override   // Fragment逻辑
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (isVisable) {
            // 当用户切换到本Fragment时，再加载数据
            Toast.makeText(getActivity(),"HomeFragment",Toast.LENGTH_SHORT).show();
        }
        super.onActivityCreated(savedInstanceState);

        findViews(); // 实例化控件

        // (先写的是这个)联网请求数据(第三方Okhttp && Okio)(D:\Android_3rd中的okhttp-master与okio-master)(需NewModlue建库,e.g:BaseUtils)
        // 号外，直接从GitHub上下的Okhttp && Okio在烧写时报各种错，这里用的是案例提供的Okhttp、Okio。
        // 别忘了添加工程依赖

        requestHomeData();  // 联网请求数据

        // 设置一些点击事件

        // 1 顶部图片点击事件
        iv_top_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String photo_url = homeDataResult.getData().getAd_list().get(0).getLink();
                //String photo_url = "http://m.ffu365.com/static/bas/1.html";
                Intent intent = new Intent(getContext(), WebDetailActivity.class);
                intent.putExtra(WebDetailActivity.URL_KEY,photo_url);
                startActivity(intent);
            }
        });

        // 2 中部图片点击事件
        iv_recommend_compary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String photo_url = homeDataResult.getData().getCompany_list().get(0).getLink();
                //String photo_url = "http://m.ffu365.com/static/bas/1.html";
                Intent intent = new Intent(getContext(), WebDetailActivity.class);
                intent.putExtra(WebDetailActivity.URL_KEY,photo_url);
                startActivity(intent);
            }
        });

        // 3 下部列表项点击事件
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int postion, long id) {

                //ListView点击事件(点击去到对应链接)(只用到了里边的postion参数)

                //点击item项，显示对应标题
                //String title =homeDataResult.getData().getNews_list().get(postion).getTitle().toString();
                //Toast.makeText(getContext(),title,Toast.LENGTH_SHORT).show();

                String list_url = homeDataResult.getData().getNews_list().get(postion).getLink();
                Intent intent = new Intent(getContext(), WebDetailActivity.class);
                intent.putExtra(WebDetailActivity.URL_KEY,list_url);
                startActivity(intent);
            }
        });
    }

    private void findViews() {
        iv_top_photo = (ImageView) view.findViewById(R.id.iv_top_photo);        //iv_top的这个id之前一直加载不过来，逼不得已ClearProject，操！
        iv_recommend_compary = (ImageView) view.findViewById(R.id.iv_recommend_compary);
        list_view = (ImplantListView) view.findViewById(R.id.list_view);
    }

    private void requestHomeData() {
        // 1 构建Okhttp客户端连接对象
        OkHttpClient okHttpClient = new OkHttpClient();
        // 2 构建参数Body为：MultipartBody.FORM 表单形式并封装参数
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("appid","1");// 用于PC看网址的(http://v2.ffu365.com/index.php?m=Api&c=Index&a=home&appid=1)
        // 3 构建一个请求，Post:提交
        Request request = new   Request.Builder()
                                       .url("http://v2.ffu365.com/index.php?m=Api&c=Index&a=home")  // 仅适合于手机，PC打开该Web无效
                                       .post(builder.build())
                                       .build();
        // 4 发送一个请求(记得要开网络权限)
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override   // 请求失败
            public void onFailure(Call call, IOException e) {
                Log.e("TAG",e+"");
            }

            @Override   // 请求成功，数据在response中，就是获取到的json字符串(LogCat->Info->TAG:进行观察)
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.e("TAG",result);

                // Gson解析
                Gson gson = new Gson();
                homeDataResult = gson.fromJson(result,HomeDataResult.class); // HomeDataResult是使用GsonFormat生成的Bean对象

                // 显示数据(上面两张InageView和下面的Listview)
                showHomeData(homeDataResult.getData());
            }
        });
    }

    // 显示首页数据
    private void showHomeData(final HomeDataResult.DataBean data) {

        handler.post(new Runnable() {
            @Override
            public void run() { // 注意：传上下文的时候是：getContext()，getContext()，getContext()，
                // getContext()，getContext()，getContext()，getContext()，getContext()，getContext()
                String dir_top_photo = data.getAd_list().get(0).getImage();     // 上方图片的json路径
                Glide.with(getContext()).load(dir_top_photo).into(iv_top_photo);

                String dir_dow_photo = data.getCompany_list().get(0).getImage();     // 下方图片的json路径
                Glide.with(getContext()).load(dir_dow_photo).into(iv_recommend_compary);

                HomeListViewAdapter adapter =new HomeListViewAdapter(getContext(),data.getNews_list());
                list_view.setAdapter(adapter);
            }
        });
    }
}
