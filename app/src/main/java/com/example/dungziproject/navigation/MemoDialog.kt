import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.dungziproject.databinding.MemoDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MemoDialog(private val currentMemo: String, private val onMemoSaved: (String) -> Unit) : DialogFragment() {

    interface MemoDialogListener {
        fun onMemoSaved(memo: String)
    }

    private var listener: MemoDialogListener? = null
    private var binding: MemoDialogBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MemoDialogListener) {
            listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = MemoDialogBinding.inflate(layoutInflater)
        binding?.memoEditText?.setText(currentMemo)

        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setView(binding?.root)
            .setPositiveButton("저장") { dialog, which ->
                val memo = binding?.memoEditText?.text.toString()
                onMemoSaved(memo)
                listener?.onMemoSaved(memo)
            }
            .setNegativeButton("취소", null)

        return dialogBuilder.create()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
