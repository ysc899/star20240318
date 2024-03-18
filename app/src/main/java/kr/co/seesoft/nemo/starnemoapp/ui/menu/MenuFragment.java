package kr.co.seesoft.nemo.starnemoapp.ui.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import kr.co.seesoft.nemo.starnemoapp.R;
import kr.co.seesoft.nemo.starnemoapp.nemoapi.po.NemoCustomerVOCCountPO;
import kr.co.seesoft.nemo.starnemoapp.nemoapi.result.NemoResultCustomerVOCCountRO;
import kr.co.seesoft.nemo.starnemoapp.nemoapi.service.NemoAPI;
import kr.co.seesoft.nemo.starnemoapp.util.AndroidUtil;

public class MenuFragment extends Fragment {

    private MenuViewModel homeViewModel;

    private TextView tvCount;
    private Handler apiVOCHandler;

    private NemoAPI api;
    private String deptCode;
    private String vocCfmDtm;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(MenuViewModel.class);
        View root = inflater.inflate(R.layout.fragment_main, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        initUI(root);
        return root;
    }

    private void initUI(View view){

        api = new NemoAPI(getContext());

        SharedPreferences sp = getContext().getSharedPreferences(AndroidUtil.TAG_SP, Context.MODE_PRIVATE);
        deptCode = sp.getString(AndroidUtil.SP_LOGIN_DEPT, "");
        vocCfmDtm = sp.getString(AndroidUtil.SP_VOC_VIEW_DATE, "");

        tvCount = (TextView) view.findViewById(R.id.tvCount);


        Bundle dataBundle = new Bundle();
        dataBundle.putString("fromMenu", "true");

        view.findViewById(R.id.btnVisitPlan).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_navigation_menu_to_visitPlanFragment,dataBundle));
        view.findViewById(R.id.btnTransaction).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_navigation_menu_to_transactionFragment,dataBundle));

        view.findViewById(R.id.btnBagSend).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_navigation_menu_to_bagsendFragment,dataBundle));
        view.findViewById(R.id.btnCustomer).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_navigation_menu_to_customerFragment,dataBundle));

        view.findViewById(R.id.btnMemo).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_navigation_menu_to_memoFragment,dataBundle));
        view.findViewById(R.id.btnContact).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_navigation_menu_to_departmentContactFragment,dataBundle));

        view.findViewById(R.id.btnVOC).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_navigation_menu_to_vocFragment,dataBundle));

        Bundle fromBundle1 = new Bundle();
        fromBundle1.putString("callType", "1");
        view.findViewById(R.id.btnItemSearch).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_navigation_menu_to_resultSearchFragment,fromBundle1));

        Bundle fromBundle2 = new Bundle();
        fromBundle2.putString("callType", "2");
        view.findViewById(R.id.btnResultSearch).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_navigation_menu_to_resultSearchFragment,fromBundle2));

        view.findViewById(R.id.btnCustomerSupport).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_navigation_menu_to_customerSupportFragment,dataBundle));

        view.findViewById(R.id.btnConfig).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_navigation_menu_to_setting));



        getVOCCount();
    }

    private void getVOCCount()
    {
        try {

            apiVOCHandler = new Handler(Looper.myLooper()){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);

                    switch (msg.what){
                        case 200:
                        case 201:
                            AndroidUtil.log("===============> " + msg.obj);

                            NemoResultCustomerVOCCountRO vocResult = (NemoResultCustomerVOCCountRO)msg.obj;

                            dispVOCCount(vocResult);

                            break;
                    }

                }
            };

            NemoCustomerVOCCountPO param = new NemoCustomerVOCCountPO();
            param.setDeptCd(deptCode);
            param.setVocCfmDtm(vocCfmDtm);

            AndroidUtil.log("NemoCustomerVOCCountPO : " + param);

            api.getVOCCount(param,apiVOCHandler);

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void dispVOCCount(NemoResultCustomerVOCCountRO vocResult)
    {

        getActivity().runOnUiThread(new Runnable() {
            public void run() {

                try
                {
                    tvCount.setVisibility(View.GONE);

                    int vocCount = vocResult.getResult();

                    if( vocCount > 0 )
                    {
                        tvCount.setVisibility(View.VISIBLE);

                        tvCount.setText(String.valueOf(vocCount));
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

    }
}