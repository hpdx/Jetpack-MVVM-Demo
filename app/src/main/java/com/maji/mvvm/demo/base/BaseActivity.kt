package com.maji.mvvm.demo.base

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.maji.mvvm.demo.R
import com.maji.mvvm.demo.base.actionbar.ActionBarHelper
import com.maji.mvvm.demo.base.layout.StatusLayout
import com.maji.mvvm.demo.base.statusbar.StatusBarCompat
import com.maji.mvvm.demo.base.statusbar.StatusBarFontColorUtils


/**
 * 带顶部导航栏的Activity基类，需要导航栏的可以继承自它
 * <p>
 * Created by android_ls on 2020/12/27 15:24.
 *
 * @author android_ls
 * @version 1.0
 */
abstract class BaseActivity<VM : ViewModel> : AppCompatActivity() {

    var mStatusLayout: StatusLayout? = null
    var mRootLayout: FrameLayout? = null

    lateinit var mActionBarHelper: ActionBarHelper
    lateinit var mViewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_layout)

        setStatusBar()
        setActionbar()
        setupViews()

        mViewModel = createViewModel()
        observeLiveData()
        loadData()
    }

    /**
     * 获取内容布局文件
     *
     * @return
     */
    protected abstract fun getContentLayoutRes(): Int

    /**
     * 指定具体执行业务逻辑的ViewModel
     */
    protected abstract fun createViewModel(): VM

    inline fun <reified T : ViewModel> getViewModel(): T {
        return ViewModelProvider(this).get(T::class.java)
    }

    /**
     * 从远程服务器端或本地加载数据
     */
    protected abstract fun loadData()

    /**
     * 观察数据源的变化，更新UI
     */
    protected abstract fun observeLiveData()

    /**
     * 设置状态栏
     */
    protected open fun setStatusBar() {
        setStatusBar(false)
    }

    /**
     * 设置状态栏
     *
     * @param translucent 是否为透明状态栏
     */
    protected open fun setStatusBar(translucent: Boolean) {
        if (translucent) {
            StatusBarCompat.translucentStatusBar(this)
        } else {
            StatusBarCompat.setStatusBarColor(this, Color.rgb(255, 255, 255))
        }
        StatusBarFontColorUtils.statusBarLightMode(this, !translucent)
    }

    /**
     * 设置ActionBar
     */
    protected open fun setActionbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        mActionBarHelper = ActionBarHelper(this, supportActionBar, toolbar)
        mActionBarHelper.toggleActionBar(true)
    }

    /**
     * ActionBar 是否要显示顶部的导航栏
     *
     * @param visible
     */
    fun setActionBar(visible: Boolean) {
        mActionBarHelper.toggleActionBar(visible)
    }

    /**
     * ActionBar 设置标题
     *
     * @param title
     */
    fun setActionBarTitle(title: String) {
        mActionBarHelper.setActionBarTitle(title)
    }

    /**
     * ActionBar 显示/隐藏返回图标
     *
     * @param displayBack
     */
    fun setActionBarBack(displayBack: Boolean) {
        mActionBarHelper.displayActionBarBack(displayBack)
    }

    /**
     * ActionBar 显示左侧的文字
     *
     * @param title
     * @param listener
     */
    fun setActionBarLeftText(title: CharSequence, listener: View.OnClickListener?) {
        mActionBarHelper.displayActionBarLeftText(title, listener)
    }

    /**
     * ActionBar 显示右侧的文字
     *
     * @param title
     * @param listener
     */
    fun setActionBarRightText(title: String, listener: View.OnClickListener) {
        mActionBarHelper.displayActionBarRightText(title, listener)
    }

    /**
     * ActionBar 显示右侧的图标
     *
     * @param resId
     * @param listener
     */
    fun setActionBarRightImage(resId: Int, listener: View.OnClickListener?) {
        mActionBarHelper.displayActionBarRightIcon(resId, listener)
    }

    /**
     * ActionBar上返回按钮事件的处理方法
     *
     * @param item
     * @return
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 初始化views，绑定事件
     */
    protected open fun setupViews() {
        mRootLayout = findViewById(R.id.fl_content_layout)
        if (mRootLayout != null) {
            val layoutInflater = LayoutInflater.from(this)
            if (hasStatusLayout()) {
                mStatusLayout = StatusLayout()
                mStatusLayout!!.apply {
                    onCreateView(layoutInflater, mRootLayout, getContentLayoutRes())
                    onViewCreated {
                        if (mStatusLayout != null) {
                            mStatusLayout!!.showLoading()
                        }
                        loadData()
                    }
                    mRootLayout!!.addView(getRootView())
                }
            } else {
                if (getContentLayoutRes() != 0) {
                    val view: View = layoutInflater.inflate(
                        getContentLayoutRes(),
                        mRootLayout,
                        false
                    )
                    mRootLayout!!.addView(view)
                }
            }
        }
    }

    /**
     * 是否需要多状态切换的Layout
     *
     * @return
     */
    protected open fun hasStatusLayout(): Boolean {
        return false
    }

    /**
     * 显示加载进度
     */
    fun showLoading() {
        mStatusLayout?.showLoading()
    }

    /**
     * 加载完成，显示内容
     */
    fun showContent() {
        mStatusLayout?.showContent()
    }

    /**
     * 加载完成，显示内容，无动画
     */
    fun showContentNoAnim() {
        mStatusLayout?.showContentNoAnim()
    }

    /**
     * 显示错误信息
     */
    fun showError(message: String) {
        mStatusLayout?.showErrorMessage(message)
    }

    /**
     * 在没有数据时，显示的空view
     */
    fun showEmpty(message: String) {
        if (mStatusLayout != null) {
            mStatusLayout!!.addEmptyView { }
            mStatusLayout!!.showEmptyMessage(message)
        }
    }

    /**
     * 在没有数据时，显示的空view
     */
    fun showEmpty(resId: Int, message: String?) {
        if (mStatusLayout != null) {
            mStatusLayout!!.addEmptyView { }
            mStatusLayout!!.showEmptyMessage(resId, message!!)
        }
    }

    /**
     * 隐藏空数据提示view
     */
    fun hideEmpty() {
        if (mStatusLayout != null) {
            mStatusLayout!!.hideEmptyView()
        }
    }

}