package com.maji.mvvm.demo.base.layout

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.maji.mvvm.demo.R

class StatusLayout {

    private var rootView: ViewGroup? = null
    private var mLoadingView: View? = null
    private var mContentView: View? = null
    private var mErrorView: View? = null
    private var tvErrorMessage: TextView? = null
    private var ivErrorIcon: ImageView? = null
    private var mEmptyView: View? = null
    private var ivEmptyIcon: ImageView? = null
    private var tvEmptyMessage: TextView? = null
    private var mLayoutInflater: LayoutInflater? = null

    fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, contentView: View): View {
        mLayoutInflater = inflater
        rootView = inflater.inflate(R.layout.page_content_view, container, false) as ViewGroup
        mContentView = contentView
        mContentView!!.id = R.id.page_content
        rootView!!.addView(contentView)
        return rootView!!
    }

    fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, layoutResId: Int): View {
        mLayoutInflater = inflater
        rootView = inflater.inflate(R.layout.page_content_view, container, false) as ViewGroup
        mContentView = inflater.inflate(layoutResId, container, false)
        mContentView!!.id = R.id.page_content
        rootView!!.addView(mContentView)
        return rootView!!
    }

    fun onViewCreated(onErrorViewClickListener: View.OnClickListener?) {
        mLoadingView = rootView!!.findViewById(R.id.loading_indicator)
        mErrorView = rootView!!.findViewById(R.id.page_error_indicator)
        ivErrorIcon = rootView!!.findViewById<View>(R.id.page_error_image) as ImageView
        tvErrorMessage = rootView!!.findViewById<View>(R.id.page_error_msg) as TextView
        mErrorView!!.setOnClickListener(onErrorViewClickListener)
    }

    fun addView(child: View) {
        rootView?.addView(child)
    }

    fun addView(child: View, layoutParams: ViewGroup.LayoutParams) {
        rootView?.addView(child, layoutParams)
    }

    /**
     * 设置出错的图标和文字
     *
     * @param resId
     * @param message
     */
    fun showErrorMessage(resId: Int, message: String) {
        ivErrorIcon?.setImageResource(resId)
        tvErrorMessage?.text = message
    }

    fun showErrorMessage(errorMessage: String) {
        tvErrorMessage?.text = errorMessage
    }

    /**
     * 设置空白页显示的图标和文字
     *
     * @param resId   图片资源Id
     * @param message 没有数据时的提示信息
     */
    fun showEmptyMessage(resId: Int, message: String) {
        ivEmptyIcon?.setImageResource(resId)
        tvEmptyMessage?.text = message
        mEmptyView?.visibility = View.VISIBLE
    }

    fun showEmptyMessage(message: String) {
        ivEmptyIcon?.visibility = View.GONE
        tvEmptyMessage?.text = message
        mEmptyView?.visibility = View.VISIBLE
    }

    fun showEmptyMessage(bitmap: Bitmap, message: String) {
        ivEmptyIcon?.setImageBitmap(bitmap)
        tvEmptyMessage?.text = message
        mEmptyView?.visibility = View.VISIBLE
    }

    fun getRootView(): ViewGroup {
        return rootView!!
    }

    val contentView: ViewGroup
        get() = rootView?.findViewById<View>(R.id.page_content) as ViewGroup

    /**
     * 添加空白页面，将空白页添加到子ViewGroup中，使用该方法
     */
    fun addEmptyView(
        container: ViewGroup,
        index: Int,
        onEmptyViewClickListener: View.OnClickListener?
    ) {
        if (mEmptyView == null) {
            mEmptyView = mLayoutInflater?.inflate(R.layout.page_empty_indicator, container, false)
            mEmptyView?.let {
                container.addView(
                    it, index, ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
                ivEmptyIcon = it.findViewById<View>(R.id.page_empty_image) as ImageView
                tvEmptyMessage = it.findViewById<View>(R.id.page_empty_msg) as TextView
                it.setOnClickListener(onEmptyViewClickListener)
            }
        }
    }

    fun addEmptyView(onEmptyViewClickListener: View.OnClickListener) {
        addEmptyView(contentView, 0, onEmptyViewClickListener)
    }

    /**
     * 隐藏空数据提示view
     */
    fun hideEmptyView() {
        mEmptyView?.visibility = View.GONE
    }

    fun showLoading() {
        mContentView?.visibility = View.GONE
        mErrorView?.visibility = View.GONE
        mLoadingView?.visibility = View.VISIBLE
    }

    @SuppressLint("ObjectAnimatorBinding")
    fun showErrorView() {
        mContentView?.visibility = View.GONE
        val set = AnimatorSet()
        val loadingIn = ObjectAnimator.ofFloat(mErrorView, "alpha", 1f)
        val loadingOut = ObjectAnimator.ofFloat(mLoadingView, "alpha", 0f)
        set.playTogether(loadingIn, loadingOut)
        set.duration = 200
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                mErrorView?.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                mLoadingView?.visibility = View.GONE
                mLoadingView?.alpha = 1f // For future showLoading calls
            }
        })
        set.start()
    }

    @SuppressLint("ObjectAnimatorBinding")
    fun showContent() {
        if (mContentView?.visibility == View.VISIBLE) {
            mErrorView?.visibility = View.GONE
            mLoadingView?.visibility = View.GONE
        } else {
            val set = AnimatorSet()
            val contentFadeIn = ObjectAnimator.ofFloat(mContentView, "alpha", 0f, 1f)
            val loadingFadeOut = ObjectAnimator.ofFloat(mLoadingView, "alpha", 1f, 0f)
            set.playTogether(contentFadeIn, loadingFadeOut)
            set.duration = 500
            set.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    mContentView?.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animator) {
                    mErrorView?.visibility = View.GONE
                    mLoadingView?.visibility = View.GONE
                    mContentView?.alpha = 1.0f
                }
            })
            set.start()
        }
    }

    fun showContentNoAnim() {
        mErrorView?.visibility = View.GONE
        mLoadingView?.visibility = View.GONE
        mContentView?.visibility = View.VISIBLE
    }

}