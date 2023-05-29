package com.example.dungziproject.navigation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.dungziproject.R
import com.example.dungziproject.databinding.AlbumItemDetailBinding
import com.example.dungziproject.databinding.FragmentAlbumBinding
import com.example.dungziproject.databinding.FragmentHomeBinding
import com.example.dungziproject.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AlbumFragment :Fragment() {
    var binding: FragmentAlbumBinding?=null
    var firestore : FirebaseFirestore?=null
    var uid :String? = null
    var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
    var contentUidList : ArrayList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        binding!!.albumRecyclerview.adapter = AlbumRecyclerViewAdapter()
        binding!!.albumRecyclerview.layoutManager = LinearLayoutManager(activity)


        binding!!.albumPopup.setOnClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.inflate(R.menu.album_popup)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId){
                    R.id.action_grid -> {
                        binding!!.albumRecyclerview.adapter = GridFragmentRecyclerViewAdapter()
                        binding!!.albumRecyclerview.layoutManager = GridLayoutManager(activity,3)
                    }
                    R.id.action_linear -> {
                        binding!!.albumRecyclerview.adapter = AlbumRecyclerViewAdapter()
                        binding!!.albumRecyclerview.layoutManager = LinearLayoutManager(activity)
                    }
                    R.id.action_upload -> {
                        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                            startActivity(Intent(requireContext(), PostingActivity::class.java))
                        }
                    }
                }
                false
            }
            popupMenu.show()
        }

        return binding!!.root
    }

    inner class AlbumRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        init {
            firestore?.collection("images")
                ?.orderBy("timestamp",Query.Direction.DESCENDING)
                ?.addSnapshotListener { querySnapshot, firebaseFireStroreException ->
                contentDTOs.clear()
                contentUidList.clear()
                for(snapshot in querySnapshot!!.documents){
                    var item = snapshot.toObject(ContentDTO::class.java)
                    contentDTOs.add(item!!)
                    contentUidList.add(snapshot.id)
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val itemBinding = AlbumItemDetailBinding.inflate(LayoutInflater.from(parent.context),parent, false)
            return CustomViewHolder(itemBinding)
        }

        inner class CustomViewHolder(val itemBinding: AlbumItemDetailBinding) : RecyclerView.ViewHolder(itemBinding.root)

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val contentDTO = contentDTOs[position]
            val customViewHolder = holder as CustomViewHolder

            customViewHolder.itemBinding.profileTextview.text = contentDTO.userId
            customViewHolder.itemBinding.idTextview.text = contentDTO.userId
            Glide.with(holder.itemView.context).load(contentDTO.imgUrl).into(holder.itemBinding.contentImageview)
            customViewHolder.itemBinding.explainTextview.text = contentDTO.explain
            customViewHolder.itemBinding.likeTextview.text =  contentDTO.favoriteCount.toString()
            customViewHolder.itemBinding.commentCountTextview.text = contentDTO.commentCount.toString()
            Glide.with(holder.itemView.context).load(contentDTO.imgUrl).into(holder.itemBinding.profileImageview)
            customViewHolder.itemBinding.favoriteImageview.setOnClickListener{
                favoriteEvent(position)
            }
            if(contentDTOs!![position].favorites.containsKey(uid)){
                customViewHolder.itemBinding.favoriteImageview.setImageResource(R.drawable.ic_favorite)
            }else{
                customViewHolder.itemBinding.favoriteImageview.setImageResource(R.drawable.ic_favorite_border)
            }

            customViewHolder.itemBinding.commentImageview.setOnClickListener{ v ->
                val intent = Intent(v.context, CommentActivity::class.java)
                intent.putExtra("contentUid",contentUidList[position])
                startActivity(intent)
            }
        }

    fun favoriteEvent(position: Int){
        var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
        firestore?.runTransaction { transaction ->

            var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

            if(contentDTO!!.favorites.containsKey(uid)){
                //좋아요 버튼 클릭 시
                contentDTO.favoriteCount = contentDTO.favoriteCount - 1
                contentDTO.favorites.remove(uid)
            }else{
                contentDTO.favoriteCount= contentDTO.favoriteCount + 1
                contentDTO.favorites[uid!!] = true
            }
            transaction.set(tsDoc,contentDTO)
        }
    }
}


    inner class GridFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()

        init {
            firestore?.collection("images")?.addSnapshotListener{querySnapshot, firebaseFirestoreException->
                if(querySnapshot == null) return@addSnapshotListener

                for(snapshot in querySnapshot.documents){
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }

                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels/3
            var imageView = ImageView(parent.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width,width)
            return CustomViewHolder(imageView)
        }

        inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView){

        }

        override fun getItemCount(): Int {
            return  contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageView = (holder as CustomViewHolder).imageView
            Glide.with(holder.imageView.context).load(contentDTOs[position].imgUrl).apply(
                RequestOptions().centerCrop()).into(imageView)

            imageView.setOnClickListener{ v ->
                val intent = Intent(v.context, CommentActivity::class.java)
                intent.putExtra("contentUid",contentUidList[position])
                startActivity(intent)
            }
        }

    }

}