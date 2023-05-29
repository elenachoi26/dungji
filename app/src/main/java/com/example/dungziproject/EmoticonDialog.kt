package com.example.dungziproject

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dungziproject.databinding.AlbumItemDetailBinding
import com.example.dungziproject.databinding.FragmentEmotionDialogBinding
import com.example.dungziproject.databinding.HomeEmoticonSelectBinding
import com.example.dungziproject.navigation.HomeFragment
import com.example.dungziproject.navigation.model.EmoticonDialogInterface


class EmoticonDialog (
    private val listener: EmoticonDialogInterface
) : DialogFragment(), EmoticonDialogInterface {
    private var binding: FragmentEmotionDialogBinding? = null
    private lateinit var emoticonList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmotionDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding?.emoticonRecyclerView?.layoutManager = GridLayoutManager(requireContext(), 2, LinearLayoutManager.HORIZONTAL, false)
        binding?.emoticonRecyclerView?.adapter = EmoticonAdapter()

        return binding?.root
    }

    override fun onEmoticonSelected(emoticon: String) {
        listener.onEmoticonSelected(emoticon)
    }





    inner class EmoticonAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        init {
            emoticonList = resources.getStringArray(R.array.emotion_arrays).toList()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val itemBinding = HomeEmoticonSelectBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return CustomViewHolder(itemBinding)
        }

        inner class CustomViewHolder(val itemBinding: HomeEmoticonSelectBinding) :
            RecyclerView.ViewHolder(itemBinding.root)

        override fun getItemCount(): Int {
            return emoticonList.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val emoticon = emoticonList[position]
            val customViewHolder = holder as CustomViewHolder // 캐스팅하여 CustomViewHolder로 사용
            val imageResId = context?.resources?.getIdentifier("@raw/" + emoticon, "raw", context!!.packageName)
            if (imageResId != null) {
                customViewHolder.itemBinding.emotionOption.setImageResource(imageResId)
            }

            customViewHolder.itemBinding.root.setOnClickListener {
                listener.onEmoticonSelected(emoticon)
                dialog?.dismiss()
            }
        }

    }
}