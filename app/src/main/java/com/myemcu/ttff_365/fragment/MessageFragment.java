package com.myemcu.ttff_365.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.myemcu.ttff_365.R;

/**
 * Created by Administrator on 2016/11/17 0017.
 */

public class MessageFragment extends Fragment {

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
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message,null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (isVisable) {
            // 当用户切换到本Fragment时，再加载数据
            Toast.makeText(getActivity(),"MessageFragment",Toast.LENGTH_SHORT).show();
        }
        super.onActivityCreated(savedInstanceState);


    }
}
