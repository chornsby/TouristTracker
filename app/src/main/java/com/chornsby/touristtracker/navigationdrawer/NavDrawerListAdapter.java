package com.chornsby.touristtracker.navigationdrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chornsby.touristtracker.R;

import java.util.ArrayList;

public class NavDrawerListAdapter extends BaseExpandableListAdapter {

    ArrayList<Group> groups = new ArrayList<>();
    private Context mContext;

    public NavDrawerListAdapter(Context context) {
        mContext = context;

        setupGroups();
    }

    public void setupGroups() {
        groups.clear();

        Group surveys = new Group();
        surveys.title = "Surveys";
        surveys.type = Group.Type.CHILDFUL;
        surveys.listItems.clear();
        surveys.listItems = new ArrayList<>();
        surveys.listItems.add(new NavDrawerListItem("Survey 1", 0));
        surveys.listItems.add(new NavDrawerListItem("Survey 2", 0));

        groups.add(surveys);

        Group submitData = new Group();
        submitData.title = "Submit data";
        submitData.type = Group.Type.CHILDLESS;
        submitData.listItems.clear();

        groups.add(submitData);

        Group help = new Group();
        help.title = "Help";
        help.type = Group.Type.CHILDLESS;
        help.listItems.clear();

        groups.add(help);

        notifyDataSetChanged();
    }

    @Override
    public int getGroupType(int groupPosition) {
        return groups.get(groupPosition).type.ordinal();
    }

    @Override
    public int getGroupTypeCount() {
        return Group.Type.values().length;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).listItems.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).listItems.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {

        Group group = groups.get(groupPosition);

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.expandable_list_group_view,
                    parent,
                    false
            );
        }

        TextView textView = (TextView) view.findViewById(R.id.group_header);
        ImageView imageView = (ImageView) view.findViewById(R.id.expandable_icon);

        textView.setText(group.title);

        if (group.type.equals(Group.Type.CHILDFUL)) {
            int imageResourceId = isExpanded ? R.drawable.ic_arrow_drop_up : R.drawable.ic_arrow_drop_down;
            imageView.setImageResource(imageResourceId);

            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.expandable_list_item_view,
                    parent,
                    false
            );

            ListItemViewHolder viewHolder = new ListItemViewHolder();
            viewHolder.title = (TextView) view.findViewById(R.id.list_item_title);

            view.setTag(viewHolder);
        }

        ListItemViewHolder viewHolder = (ListItemViewHolder) view.getTag();
        NavDrawerListItem listItem = (NavDrawerListItem) getChild(groupPosition, childPosition);

        viewHolder.title.setText(listItem.getTitle());

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public static class Group {
        public Type type;
        public String title;
        public ArrayList<NavDrawerListItem> listItems = new ArrayList<>();

        public enum Type {
            CHILDLESS, CHILDFUL
        }
    }

    class ListItemViewHolder {
        TextView title;
        // TextView notifications;
    }
}
