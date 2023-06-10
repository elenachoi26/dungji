package com.example.dungziproject

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
import com.example.dungziproject.databinding.FragmentProfileimgDialogBinding
import com.example.dungziproject.databinding.HomeEmoticonSelectBinding
import com.example.dungziproject.navigation.model.ItemDialogInterface

class ProfileImageDialog (
    private val listener: ItemDialogInterface
) : DialogFragment(), ItemDialogInterface {
    private var binding: FragmentProfileimgDialogBinding? = null
    private lateinit var roleList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileimgDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding?.profileImageRecyclerView?.layoutManager = GridLayoutManager(requireContext(), 3, LinearLayoutManager.HORIZONTAL, false)
        binding?.profileImageRecyclerView?.adapter = ProfileImgAdapter()

        return binding?.root
    }

    override fun onItemSelected(item: String) {
        listener.onItemSelected(item)
    }


    inner class ProfileImgAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        init {
            roleList = resources.getStringArray(R.array.role_arrays).toList()
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
            return roleList.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val role = roleList[position]
            val customViewHolder = holder as CustomViewHolder // 캐스팅하여 CustomViewHolder로 사용
            val imageResId = context?.resources?.getIdentifier("@raw/" + role, "raw", context!!.packageName)
            if (imageResId != null) {
                customViewHolder.itemBinding.emotionOption.setImageResource(imageResId)
            }

            customViewHolder.itemBinding.root.setOnClickListener {
                listener.onItemSelected(role)
                dialog?.dismiss()
            }
        }

    }
}