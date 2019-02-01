package com.lagel.com.main.tab_fragments;

import android.opengl.Visibility;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lagel.com.R;
import com.lagel.com.adapter.ViewPagerAdapter;
import com.lagel.com.main.activity.HomePageActivity;
import com.lagel.com.mqttchat.Fragments.BuyingFragment;
import com.lagel.com.mqttchat.Fragments.SealingFragment;

/**
 * <h2>ChatFrag</h2>
 * <P>
 *     Chat fragment ot show the details.
 * </P>
 * @since 3/31/2017.
 */
public class ChatFrag extends Fragment implements View.OnClickListener
{
    private BuyingFragment buyingFragment;
    private SealingFragment sealingFragment;
    private ViewPager chatViewPager;
    private EditText search_text;
    private RelativeLayout search_intiate,search_edit_view;
    private HomePageActivity homePageActivity;
    private boolean firstTime = true;
    public TabLayout tabs_chat;
    public TextView tvtab0,tvtab1;
    public ImageView tabBadgeDot0,tabBadgeDot1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        buyingFragment=new BuyingFragment();
        sealingFragment=new SealingFragment();
        homePageActivity=(HomePageActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frag_chat, container, false);
        chatViewPager= (ViewPager) view.findViewById(R.id.viewpager);
        tabs_chat= (TabLayout) view.findViewById(R.id.tabs_chat);
        setupViewPager();
        tabs_chat.setupWithViewPager(chatViewPager);

        setCustomTab0();
        setCustomTab1();

        search_intiate=(RelativeLayout)view.findViewById(R.id.search_intiate);
        search_intiate.setOnClickListener(this);
        search_edit_view=(RelativeLayout)view.findViewById(R.id.search_edit_view);
        view.findViewById(R.id.search_icon).setOnClickListener(this);
        view.findViewById(R.id.refreshButton).setOnClickListener(this);
        view.findViewById(R.id.close_icon).setOnClickListener(this);
        search_text=(EditText)view.findViewById(R.id.search_text);
        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                checkSearchFiltre(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        return view;
    }

    private void setCustomTab0() {

        TabLayout.Tab tab = tabs_chat.getTabAt(0);
        View v2=null;
        tab.setCustomView(v2);
        View tabView = ((ViewGroup) tabs_chat.getChildAt(0)).getChildAt(0);
        tabView.requestLayout();
        View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.single_chat_tab_view, null);
        tvtab0=(TextView)view1.findViewById(R.id.tab_name);
        tabBadgeDot0=(ImageView)view1.findViewById(R.id.tab_badge_dot);
        tvtab0.setText(getString(R.string.buying));
        tvtab0.setTextColor(getResources().getColor(R.color.colorPrimary));
        tab.setCustomView(view1);
    }

    private void setCustomTab1() {

        TabLayout.Tab tab = tabs_chat.getTabAt(1);
        View v2=null;
        tab.setCustomView(v2);
        View tabView = ((ViewGroup) tabs_chat.getChildAt(0)).getChildAt(0);
        tabView.requestLayout();
        View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.single_chat_tab_view, null);

        tvtab1=(TextView)view1.findViewById(R.id.tab_name);
        tabBadgeDot1=(ImageView)view1.findViewById(R.id.tab_badge_dot);
        tvtab1.setText(getString(R.string.selling));
        tvtab1.setTextColor(getResources().getColor(R.color.tab_unselected_color));

        tab.setCustomView(view1);
    }



    /*
     *Doing the setup for the view pager. */
    private void setupViewPager()
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(buyingFragment,getResources().getString(R.string.buying));
        adapter.addFragment(sealingFragment,getResources().getString(R.string.selling));
        chatViewPager.setAdapter(adapter);
        chatViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position)
            {
                if(position==0){
                    tvtab0.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tvtab1.setTextColor(getResources().getColor(R.color.tab_unselected_color));
                }else {
                    tvtab1.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tvtab0.setTextColor(getResources().getColor(R.color.tab_unselected_color));
                }
                search_text.setText("");
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.close_icon:
                search_text.setText("");
                animateSearchView();
                break;
            case R.id.search_intiate:
                animateSearchView();
                break;
            case R.id.search_icon:
                animateSearchView();
                break;
            case R.id.refreshButton:
                search_text.setText("");
                handelRefreshCall();
                break;
        }
    }
    /*
     *Handling the refresh call call to the  */
    private void handelRefreshCall()
    {
        switch (chatViewPager.getCurrentItem())
        {
            case 0:
                buyingFragment.performChatSync();
                break;
            case 1:
                sealingFragment.performChatSync();
                break;

        }
    }
    /*
     *searching*/
    private void checkSearchFiltre(String searchText)
    {
        switch (chatViewPager.getCurrentItem())
        {
            case 0:
                buyingFragment.performFiltre(searchText);
                break;
            case 1:
                sealingFragment.performFiltre(searchText);
                break;
        }
    }
    /*
     *Animating the chat view. */
    private void animateSearchView()
    {
        Animation animationUtils;
        if(search_edit_view.getVisibility()==View.GONE)
        {
            search_edit_view.setVisibility(View.VISIBLE);
            search_intiate.setVisibility(View.GONE);
            animationUtils=AnimationUtils.loadAnimation(getActivity(), R.anim.search_view_animaton);
            search_edit_view.setAnimation(animationUtils);
        }else
        {
            search_edit_view.setVisibility(View.GONE);
            search_intiate.setVisibility(View.VISIBLE);
        }

    }

}
