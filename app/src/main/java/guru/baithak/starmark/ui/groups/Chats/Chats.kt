package guru.baithak.starmark.ui.groups.Chats


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import guru.baithak.starmark.Helpers.groupName
import guru.baithak.starmark.Models.Groups

import guru.baithak.starmark.R
import kotlinx.android.synthetic.main.fragment_chats.*


class Chats : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var groupData = arguments!![groupName] as Groups
        if(groupData != null){
            topicFragRecycler.adapter = TopicListAdapter(context!!, groupData.topics)
            topicFragRecycler.layoutManager = LinearLayoutManager(context)
        }



    }
}
