package com.ossul.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ossul.R;
import com.ossul.activities.BaseActivity;
import com.ossul.apppreferences.AppPreferences;
import com.ossul.network.model.response.Comment;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.view.CircularImageView;

import java.util.ArrayList;

/***
 * Adapter to set data on comment
 ***/
public class CommentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_USERCHAT = 0;
    private static final int TYPE_OTHERUSERCHAT = 1;
    private BaseActivity baseActivity;
    private ArrayList<Comment> list;

    public CommentRecyclerAdapter(BaseActivity baseActivity, ArrayList<Comment> listComment) {
        this.baseActivity = baseActivity;
        this.list = listComment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_USERCHAT) {
            View view = LayoutInflater.from(baseActivity).inflate(R.layout.layout_comment_item, parent, false);
            return new VH_User(view);
        } else if (viewType == TYPE_OTHERUSERCHAT) {
            View view = LayoutInflater.from(baseActivity).inflate(R.layout.item_otheruser_chatmsg, parent, false);
            return new VH_OtherUser(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VH_User) {
            Comment chatMessage = list.get(position);
//            ImageLoader.getInstance().displayImage(chatMessage.getUserAvatar(), ((VH_User) holder).);
            ((VH_User) holder).tv_userName_Date.setText(String.format("%s | %s", chatMessage.commentedBy, AppUtilsMethod.formatToYesterdayOrToday(chatMessage.creationDate)));
            ((VH_User) holder).tv_comment.setText(chatMessage.commentText);
        } else if (holder instanceof VH_OtherUser) {
            final Comment chatMessage = list.get(position);
            ((VH_OtherUser) holder).tv_userName_Date.setText(String.format("%s | %s", chatMessage.commentedBy, AppUtilsMethod.formatToYesterdayOrToday(chatMessage.creationDate)));
            ((VH_OtherUser) holder).tv_comment.setText(chatMessage.commentText);
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).userId == Long.parseLong(AppPreferences.get().getUserId()))
            return TYPE_USERCHAT;
        return TYPE_OTHERUSERCHAT;
    }

    class VH_User extends RecyclerView.ViewHolder {

        public CircularImageView iv_userPhoto;
        public TextView tv_userName_Date;
        public TextView tv_comment;

        public VH_User(View itemView) {
            super(itemView);
            iv_userPhoto = (CircularImageView) itemView.findViewById(R.id.civ_userIconChatMsg);
            tv_userName_Date = (TextView) itemView.findViewById(R.id.txt_userName);
            tv_comment = (TextView) itemView.findViewById(R.id.txt_userChatMsg);
        }
    }

    class VH_OtherUser extends RecyclerView.ViewHolder {

        public CircularImageView iv_userPhoto;
        public TextView tv_userName_Date;
        public TextView tv_comment;

        public VH_OtherUser(View itemView) {
            super(itemView);

            iv_userPhoto = (CircularImageView) itemView.findViewById(R.id.civ_otherUserIconChatMsg);
            tv_userName_Date = (TextView) itemView.findViewById(R.id.txt_othrUserName);
            tv_comment = (TextView) itemView.findViewById(R.id.txt_otherUserChatMsg);
        }
    }

}
