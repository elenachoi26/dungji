package com.example.dungziproject.navigation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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
import com.example.dungziproject.navigation.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AlbumFragment :Fragment() {
    var binding: FragmentAlbumBinding?=null
    var firestore : FirebaseFirestore?=null
    var uid :String? = null
    var flag=true
    private lateinit var auth: FirebaseAuth
    var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
    var contentUidList : ArrayList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        binding = FragmentAlbumBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        binding!!.albumRecyclerview.adapter = AlbumRecyclerViewAdapter()
        binding!!.albumRecyclerview.layoutManager = LinearLayoutManager(activity)

        //popup menu
        binding!!.layoutBtn.setOnClickListener {
            if(flag){
                binding!!.layoutBtn.setImageResource(R.drawable.ic_list)
                binding!!.albumRecyclerview.adapter = GridFragmentRecyclerViewAdapter()
                binding!!.albumRecyclerview.layoutManager = GridLayoutManager(activity,3)
                flag = false
            }else{
                binding!!.layoutBtn.setImageResource(R.drawable.ic_grid)
                binding!!.albumRecyclerview.adapter = AlbumRecyclerViewAdapter()
                binding!!.albumRecyclerview.layoutManager = LinearLayoutManager(activity)
                flag = true
            }

        }

        binding!!.uploadBtn.setOnClickListener {
            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                startActivity(Intent(requireContext(), PostingActivity::class.java))
            }
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

        fun convertEmailToValidPath(email: String): String {
            // 특수 문자를 제거하고 적절한 형식으로 변환
            val path = email.replace(".", "_dot_")
                .replace("#", "_hash_")
                .replace("$", "_dollar_")
                .replace("[", "_leftBracket_")
                .replace("]", "_rightBracket_")
            return path
        }


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val contentDTO = contentDTOs[position]
            val customViewHolder = holder as CustomViewHolder
            //닉네임
            customViewHolder.itemBinding.profileTextview.text = contentDTO.nickname
            //게시물 사진
            Glide.with(holder.itemView.context).load(contentDTO.imgUrl).into(holder.itemBinding.contentImageview)
            //설명
            customViewHolder.itemBinding.explainTextview.text = contentDTO.explain
            //좋아요 개수
            customViewHolder.itemBinding.likeTextview.text =  contentDTO.favoriteCount.toString()
            //댓글 개수
//            customViewHolder.itemBinding.commentCountTextview.text = contentDTO.commentCount.toString()

            val dateFormat = SimpleDateFormat("MM월 dd일", Locale.getDefault())
            customViewHolder.itemBinding.dateTextview.text = dateFormat.format(contentDTO?.timestamp?.let { Date(it) })

            //프로필 사진
            //val userRef = FirebaseDatabase.getInstance().getReference("user").child(contentDTO?.userId?:"")
            val convertedEmail = convertEmailToValidPath(contentDTO?.userId ?: "")
            val userRef = FirebaseDatabase.getInstance().getReference("user").child(convertedEmail)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    var resId = resources.getIdentifier("@raw/" + user?.image, "raw", requireContext().packageName)
                    customViewHolder.itemBinding.profileImageview.setImageResource(resId)
                }

                override fun onCancelled(error: DatabaseError) {
                    // 처리할 작업을 추가하세요
                }
            })




            //좋아요 클릭 이벤트
            customViewHolder.itemBinding.favoriteImageview.setOnClickListener{
                favoriteEvent(position)
            }
            if(contentDTOs!![position].favorites.containsKey(uid)){
                customViewHolder.itemBinding.favoriteImageview.setImageResource(R.drawable.ic_favorite)
            }else{
                customViewHolder.itemBinding.favoriteImageview.setImageResource(R.drawable.ic_favorite_border)
            }

            customViewHolder.itemBinding.contentImageview.setOnClickListener{ v ->
//            customViewHolder.itemBinding.
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
            firestore?.collection("images")?.orderBy("timestamp", Query.Direction.DESCENDING)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException->
                    if(querySnapshot == null) return@addSnapshotListener
                    contentDTOs.clear()
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
            val imageView = (holder as CustomViewHolder).imageView
            Glide.with(holder.imageView.context)
                .load(contentDTOs[position].imgUrl)
                .apply(RequestOptions().centerCrop())
                .into(imageView)

            imageView.setOnClickListener { v ->
                val intent = Intent(v.context, CommentActivity::class.java)
                intent.putExtra("contentUid", contentUidList[position])
                startActivity(intent)
            }
        }
    }
}