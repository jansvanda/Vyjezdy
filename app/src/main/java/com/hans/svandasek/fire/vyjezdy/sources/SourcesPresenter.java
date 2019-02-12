package com.hans.svandasek.fire.vyjezdy.sources;

import android.content.Context;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hans.svandasek.fire.vyjezdy.models.SourceItem;

import java.util.List;

/**
 * Created by Kartik_ch on 11/7/2015.
 */
public class SourcesPresenter implements ISourcePresenter, OnSourceSavedListener, OnSourcesLoadedListener, OnSourcesModifyListener {

    private ISourceView mISourceView;
    private SourceInteractor mSourceInteractor;
    private EditText mETxtSourceName, mETxtSourceUrl;
    private TextView mTxtCategory;
    private ImageView mImgCategory;
    private FrameLayout mFrameCategory;

    public SourcesPresenter(ISourceView mISourceView, Context mContext) {
        this.mISourceView = mISourceView;
        this.mSourceInteractor = new SourceInteractor(mContext);
    }

    public void addSource(SourceItem sourceItem) {
        mSourceInteractor.addSourceToDb(this, sourceItem);
    }

    public void getSources() {
        mSourceInteractor.getSourcesFromDb(this);
    }

    public void getSourceItems() {
        mSourceInteractor.getSourceItemsFromDb(this);
    }


    //public void deleteSource(SourceItem sourceItem) {
    //    mSourceInteractor.deleteSourceItemFromDb(SourcesPresenter.this, sourceItem);
    //}

    @Override
    public void onSuccess(String message) {
        mISourceView.dataSourceSaved(message);

    }

    @Override
    public void onFailure(String message) {
        mISourceView.dataSourceSaveFailed(message);
    }

    @Override
    public void onSourceLoaded(List<String> sourceNames) {
        mISourceView.dataSourceLoaded(sourceNames);
    }

    @Override
    public void onSourceItemsLoaded(List<SourceItem> sourceItems) {
        mISourceView.dataSourceItemsLoaded(sourceItems);
    }

    @Override
    public void onSourceLoadingFailed(String message) {
        mISourceView.dataSourceLoadingFailed(message);
    }

    @Override
    public void onSourceModified(SourceItem sourceItem, String oldName) {
        mISourceView.sourceItemModified(sourceItem, oldName);
    }

    @Override
    public void onSourceModifiedFailed(String message) {
        mISourceView.sourceItemModificationFailed(message);
    }

    @Override
    public void onSourceDeleted(SourceItem sourceItem) {
        mISourceView.sourceItemDeleted(sourceItem);
    }

    @Override
    public void onSourceDeletionFailed(String message) {
        mISourceView.sourceItemDeletionFailed(message);
    }
}
