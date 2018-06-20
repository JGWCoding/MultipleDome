package ziweiyang.toppine.com.oschinadome.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

import ziweiyang.toppine.com.oschinadome.AppConfig;
import ziweiyang.toppine.com.oschinadome.OSCApplication;
import ziweiyang.toppine.com.oschinadome.adapter.BaseRecyclerAdapter;
import ziweiyang.toppine.com.oschinadome.adapter.NewsSubAdapter;
import ziweiyang.toppine.com.oschinadome.bean.Author;
import ziweiyang.toppine.com.oschinadome.bean.PageBean;
import ziweiyang.toppine.com.oschinadome.bean.ResultBean;
import ziweiyang.toppine.com.oschinadome.bean.SubBean;
import ziweiyang.toppine.com.oschinadome.bean.SubTab;
import ziweiyang.toppine.com.oschinadome.ui.view.EventHeaderView;
import ziweiyang.toppine.com.oschinadome.ui.view.HeaderView;
import ziweiyang.toppine.com.oschinadome.ui.view.NewsHeaderView;
import ziweiyang.toppine.com.oschinadome.utils.TDevice;
/**
 * {
 * "banner": {
 * "catalog": 1,
 * "href": "https://www.oschina.net/action/apiv2//banner?catalog=1"
 * },
 * "fixed": true,
 * "href": "https://www.oschina.net/action/apiv2/sub_list?token=d6112fa662bc4bf21084670a857fbd20",
 * "name": "开源资讯",
 * "needLogin": false,
 * "isActived": true,
 * "order": 1,
 * "subtype": 1,
 * "token": "d6112fa662bc4bf21084670a857fbd20",
 * "type": 6
 * }
 */

/**
 * 属于DynamicTabFragment里的Viewpager的Fragment
 * 每日乱弹 界面 (RecyclerView界面)
 * 根据SubBean来使用不同getRecyclerAdapter();
 */

public class SubFragment extends BaseGeneralRecyclerFragment<SubBean> {
    private SubTab mTab;
    private HeaderView mHeaderView;
    private OSCApplication.ReadState mReadState;

    public static SubFragment newInstance(Context context, SubTab subTab) {
        SubFragment fragment = new SubFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("sub_tab", subTab);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        mTab = (SubTab) bundle.getSerializable("sub_tab");
        CACHE_NAME = mTab.getToken();
    }

    @Override
    public void initData() {
        mReadState = OSCApplication.getReadState("sub_list");
        if (mTab.getBanner() != null) {
            mHeaderView = mTab.getBanner().getCatalog() == SubTab.BANNER_CATEGORY_NEWS ?
                    new NewsHeaderView(mContext, getImgLoader(), mTab.getBanner().getHref(), mTab.getToken() + "banner" + mTab.getType()) :
                    new EventHeaderView(mContext, getImgLoader(), mTab.getBanner().getHref(), mTab.getToken() + "banner" + mTab.getType());
        }
        super.initData();
        mAdapter.setHeaderView(mHeaderView);
        mAdapter.setSystemTime(AppConfig.getAppConfig(getActivity()).get("system_time"));
        if (mAdapter instanceof NewsSubAdapter) {
            ((NewsSubAdapter) mAdapter).setTab(mTab);
        }
    }

    @Override
    public void onItemClick(int position, long itemId) {    //item点击跳转详情Activity
        if (!TDevice.hasWebView(mContext))
            return;
        SubBean sub = mAdapter.getItem(position);
        if (sub == null)
            return;
//        switch (sub.getType()) {  //TODO 注释掉了
//            case News.TYPE_SOFTWARE:
//                //SoftwareDetailActivity.show(mContext, sub.getId());
//                SoftwareDetailActivity.show(mContext, sub);
//                break;
//            case News.TYPE_QUESTION:
//                //QuestionDetailActivity.show(mContext, sub.getId());
//                QuestionDetailActivity.show(mContext, sub);
//                break;
//            case News.TYPE_BLOG:
//                //BlogDetailActivity.show(mContext, sub.getId());
//                BlogDetailActivity.show(mContext, sub);
//                break;
//            case News.TYPE_TRANSLATE:
//                //TranslateDetailActivity.show(mContext, sub.getId());
//                NewsDetailActivity.show(mContext, sub);
//                break;
//            case News.TYPE_EVENT:
//                //EventDetailActivity.show(mContext, sub.getId());
//                EventDetailActivity.show(mContext, sub);
//                break;
//            case News.TYPE_NEWS:
//                //NewsDetailActivity.show(mContext, sub.getId());
//                NewsDetailActivity.show(mContext, sub);
//                break;
//            default:
//                UIHelper.showUrlRedirect(mContext, sub.getHref());
//                break;
//        }

        mReadState.put(sub.getKey());
        mAdapter.updateItem(position);
    }

    @Override
    public void onRefreshing() {
        super.onRefreshing();
        if (mHeaderView != null)
            mHeaderView.requestBanner();
    }

    @Override   //请求数据  -----
    protected void requestData() {
//        OSChinaApi.getSubscription(mTab.getHref(), isRefreshing ? null : mBean.getNextPageToken(), mHandler);
        onRequestError();
    }

    @Override
    protected void onRequestError() {//由于没有接口,给予自己数据
        if (getRecyclerAdapter() instanceof NewsSubAdapter) {
                ResultBean<PageBean<SubBean>> resultBean = new ResultBean<>();
                resultBean.setCode(1);
                resultBean.setMessage("我给予请求成功");
                resultBean.setTime(new Date().getTime() + "");
                PageBean<SubBean> bean = new PageBean<>();
                ArrayList<SubBean> list = new ArrayList<>();
                Author author = new Author();
                for (int i = 0; i < 20; i++) {
                    SubBean subBean = new SubBean();
                    if (i < 4) {
                        subBean.setTitle("Krita 4.0.4 正式发布，开源数字绘画软件");
                        subBean.setBody("Krita 开发小组在今天正式发布 Krita 4.0.4 版，这是 Krita 4.0.0 的一个问题修正版本，也是该系列的最后一次维护性更新。 Krita 的中文翻译在这一版得到了大幅更新，但是少数字符串因为在源代码中未进行可翻译处理而依然显示英文。这个问题正在被处理。果遇到其它翻译缺失也请上报。(BUG:395338) 以下是在 Krita 4.0.4 版的修复问题列表： OpenColorIO 在 Mac OSX 下面可以工作了 修复了像素笔刷在透明度图层上绘画时的图像问题 (BUG:394438) 修复了在使用生成器图层时的竞态紊乱问题 修复了编辑变形蒙版时的一个崩溃问题 (BUG:395224) 添加预设记忆到“十大笔刷”脚本，让来回切换预设变得更为便利。 改进描边图层样式的性能 (BUG:361130, BUG:390985) 不允许在.kra 文件中进行嵌套：在一个.kra文件里嵌入一个本身带有文件图层的.kra文件作为文件图层会扰乱文件加载。 使用阈值滤镜时保持透明度通道 (BUG:394235) 不自动将资源包的名称用作标签 (BUG:394345) 修复在 Python 调色板工具面板中选择颜色 (BUG:394705) 在启动 Krita 时恢复上次使用的颜色，但在创建新视图时不恢复 (BUG:394816) 允许在当前选中的节点是蒙版时创建一个分组图层。 (BUG:394832) 在分段渐变编辑器中显示正确的透明度 (BUG:394887) 移除旧文本工具和艺术字工具曾经使用过的已过时的快捷键 (BUG:393508) 允许用分数设定多重笔刷的角度 改进 OpenGL 画布的性能，特别时 Mac OSX 修复在孤立图层模式下在穿透属性的分组图层里绘画 (BUG:394437) 改进 OpenEXR 文件的加载性能 (Jeroen Hoolmans 贡献补丁) 现在 Krita 在忙碌时依然能够进行自动保存 改进默认语言的加载 修复双击时的颜色选择问题 (BUG:394396) 修复呼叫 FFMpeg 时帧数不一致的问题 (BUG:389045) 修复 Mac OSX 下使用 16 位浮点或者 32 位浮点通道时红蓝通道对调的问题 在比较新的 Qt 版本下面修复接受触控事件 修复 Breeze 主题整合：Krita 不再尝试在线程中创建小工具 (BUG:392190) 修复在通过 Python 加载图像时的批处理警告 在 Windows 和 Mac OSX 上加载系统色彩配置 修复 Mac OSX 的一个崩溃问题 (BUG:394068) 下载 Windows 版本 Windows 用户请注意：如果你遇到了崩溃，请遵循此处的说明来使用软件调试符号包，这样可以帮助我们找到崩溃的原因。 64 位 Windows 安装程序：krita-x64-4.0.4-setup.exe 64 位 Windows 压缩包：krita-x64-4.0.4.zip 64 位软件调试符号包 (解压到 Krita 的安装目录)...");
                    } else {
                        subBean.setTitle("Title" + i);
                        subBean.setBody("body" + i);
                    }
                    subBean.setHref("https://my.oschina.net/tysontan");
                    subBean.setPubDate("2018-05-18 12:01:01");
                    subBean.setType(0);
                    author.setName("Name:"+i);
                    subBean.setAuthor(author);
                    list.add(subBean);
                }
                bean.setItems(list);
                resultBean.setResult(bean);
                setListData(resultBean);
        } else {
        }
            super.onRequestError();
    }

    @Override
    protected void setListData(ResultBean<PageBean<SubBean>> resultBean) {
        super.setListData(resultBean);
        mAdapter.setSystemTime(resultBean.getTime());
    }

    @Override
    protected BaseRecyclerAdapter<SubBean> getRecyclerAdapter() {
        int mode = mHeaderView != null ? BaseRecyclerAdapter.BOTH_HEADER_FOOTER : BaseRecyclerAdapter.ONLY_FOOTER;//ONLY_FOOTER
//        if (mTab.getType() == News.TYPE_BLOG) // TODO 注释掉了
//            return new BlogSubAdapter(getActivity(), mode);
//        else if (mTab.getType() == News.TYPE_EVENT)
//            return new EventSubAdapter(this, mode);
//        else if (mTab.getType() == News.TYPE_QUESTION)
//            return new QuestionSubAdapter(this, mode);
        //第一次返回这个 返回这个Adapter mode=3 BOTH_HEADER_FOOTER 这里面初始化了mState = STATE_HIDE;为加载中状态
        return new NewsSubAdapter(getActivity(), mode);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<SubBean>>>() {
        }.getType();
    }

    @Override
    protected Class<SubBean> getCacheClass() {
        return SubBean.class;
    }
}
