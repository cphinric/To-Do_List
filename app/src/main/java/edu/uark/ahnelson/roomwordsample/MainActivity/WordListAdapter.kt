package edu.uark.ahnelson.roomwordsample.MainActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.uark.ahnelson.roomwordsample.Model.Word
import edu.uark.ahnelson.roomwordsample.R

class WordListAdapter(val wordClicked:(word:Word)->Unit, val wordDeleted: (word: Word) -> Unit): ListAdapter<Word, WordListAdapter.WordViewHolder>(WordsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        return WordViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val current = getItem(position)

        holder.bind(current.word,current.quantity,current.done)
        holder.itemView.tag= current
        holder.itemView.setOnClickListener{
            wordClicked(holder.itemView.tag as Word)
        }

        // Handle the "Done?" button click
        holder.doneBox.setOnClickListener {
            wordDeleted(current)
            current.done = true
            notifyDataSetChanged()

        }
    }

    class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val wordItemView: TextView = itemView.findViewById(R.id.itemText)
        private val quantityTextView: TextView = itemView.findViewById(R.id.quantityText)
        val doneBox: Button = itemView.findViewById(R.id.doneBox)

        fun bind(text: String?,quantity:Int?, done: Boolean) {
            wordItemView.text = text
            quantityTextView.text = quantity.toString()

            //Check if the task is completed and set the text color accordingly
            if(done) {
                wordItemView.setTextColor(ContextCompat.getColor(itemView.context, R.color.completed))
            } else {
                wordItemView.setTextColor(ContextCompat.getColor(itemView.context, R.color.incomplete))
            }
        }
        companion object {
            fun create(parent: ViewGroup): WordViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return WordViewHolder(view)
            }
        }
    }

    class WordsComparator : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.word == newItem.word
        }
    }
}
