package com.mta.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mta.main.R;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BaseListAdapter extends BaseAdapter {

	private Map<String,Integer> viewTypeResources = new HashMap<String, Integer>();
	private Map<String,View> typeViews = new HashMap<String, View>();
	private ArrayList<ContentValues> data;
	public static final String DEFAULT = "default";
	public static final String ID = "id";
	
	private String targetField;
	private int TYPE_TAG;
    private LayoutInflater inflater;
    private Context context;
	
    public BaseListAdapter(Context context) {
    	this.context = context;
    	inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	TYPE_TAG = R.string.base_tag;
    }
    
    /**
     * Sets view type identifier in data set.
     * @param target key view.
     */
	public void setTypeTargetField(String target){
		this.targetField = target;
	}
    /**
     * @return view type identifier in data set.
     */
	public String getTypeTargetField(){
		return this.targetField;
	}
	
	/**
	 * Add view to hold a specific list item.
	 * @param viewName name for the view
	 * @param resourceId identifier of the view
	 */
	public void addViewType(String viewName, int resourceId){
		this.viewTypeResources.put(viewName, resourceId);
	}
	
	/**
	 * Add one element to the list
	 * @param data Single data element.
	 */
	public void addDataElement(ContentValues data){
		this.data.add(data);
	}
	
	/**
	 * Set data for the selected list.
	 * @param data Data set to be added to the list.
	 */
	public void setData(ArrayList<ContentValues> data){
		this.data = data;
	}

	@Override
	public int getCount() {
		return this.data.size();
	}

	@Override
	public ContentValues getItem(int position) {
		return this.data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(this.targetField == null){
			if(convertView != null){
				return applyData(convertView, position);
			}
			else{
                if( ! viewTypeResources.containsKey(DEFAULT)){
                    throw new RuntimeException("View resource type should be default when target field not available");
                }
				int resource = viewTypeResources.get(DEFAULT);
	            convertView = inflater.inflate(resource, null);
	            return applyData(convertView, position);
			}
		}
		String viewTypeName = data.get(position).getAsString(this.targetField);
		if(convertView != null){
			String viewTypeNameCurrent = (String) convertView.getTag(TYPE_TAG);
			if(viewTypeNameCurrent.equals(viewTypeName)){
	            return applyData(convertView, position);
			}/* else if(typeViews.containsKey(viewTypeName)) {
                convertView = typeViews.get(viewTypeName);
                return applyData(convertView, position);
            }*/
		}
    	int resource = viewTypeResources.get(viewTypeName);
        convertView = inflater.inflate(resource, null);
        convertView.setTag(TYPE_TAG, viewTypeName);
		this.typeViews.put(viewTypeName, convertView);
        return applyData(convertView, position);
	}

	private View applyData(View convertView, int position) {
		ContentValues values = data.get(position);
		for(String value : values.keySet()){
			int resourceID = context.getResources().getIdentifier(value, ID, context.getPackageName());
			if(resourceID != 0){
			    TextView textView = (TextView) convertView.findViewById(resourceID);
                if(textView != null){
                    textView.setText(values.getAsString(value));
                }
			}
		}
		return convertView;
	}

}
