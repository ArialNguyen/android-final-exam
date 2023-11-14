import android.graphics.Canvas
import android.graphics.Point
import android.view.View
import android.view.View.DragShadowBuilder
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.models.Term


enum class TypeFlashCard {
    LEARNING, KNEW, EXIT, START, END, LOCATION
}

class CustomDragShadowBuilder(private val view: View) : DragShadowBuilder(view) {

    private val tvBackground: TextView = view.findViewById(R.id.tvBackground_Flashcard)
    private val tvAboveBG: TextView = view.findViewById(R.id.tvAboveBG_Flashcard)
    private val cardBackground: CardView = view.findViewById(R.id.cardBackground_flashcard)
    private val tvFront: TextView = (view.parent as ViewGroup).findViewById(R.id.tvFront_Flashcard)
    private val tvBack: TextView = (view.parent as ViewGroup).findViewById(R.id.tvBack_Flashcard)

    override fun onDrawShadow(canvas: Canvas?) {
        super.onDrawShadow(canvas)
    }

    public fun updateDragShadow(typeFlashCard: TypeFlashCard) {
        if (typeFlashCard.name == TypeFlashCard.START.name) {
            tvAboveBG.text = ""
            tvFront.visibility = View.INVISIBLE
            cardBackground.setBackgroundResource(R.drawable.border_parent)
        } else if (typeFlashCard.name == TypeFlashCard.LEARNING.name) {
            cardBackground.setBackgroundResource(R.drawable.layout_learning)
            tvAboveBG.text = "Đang học"
            tvAboveBG.setTextColor(view.resources.getColor(R.color.text_Learning))
        } else if (typeFlashCard.name == TypeFlashCard.KNEW.name) {
            cardBackground.setBackgroundResource(R.drawable.layout_knew)
            tvAboveBG.text = "Đã biết"
            tvAboveBG.setTextColor(view.resources.getColor(R.color.text_Knew))
        } else if (typeFlashCard.name == TypeFlashCard.EXIT.name) {
            cardBackground.setBackgroundResource(R.drawable.border_parent)
            tvAboveBG.text = ""
            tvAboveBG.alpha = 1F
            tvBackground.alpha = 1F
        } else {
            cardBackground.setBackgroundResource(R.drawable.border_parent)
            tvAboveBG.text = ""
            tvAboveBG.alpha = 1F
            tvFront.visibility = View.VISIBLE
            tvBackground.alpha = 1F
        }
        view.updateDragShadow(DragShadowBuilder(view))
    }

    fun updateWithPosition(typeFlashCard: TypeFlashCard, currentX: Float, widthFixed: Float) {
        val halfWidth = widthFixed / 2;
        var percentage: Float = 0F
        if (typeFlashCard.name == TypeFlashCard.LEARNING.name) {
            if (currentX >= halfWidth) {
                percentage = (currentX - halfWidth) / (halfWidth)
                tvAboveBG.alpha = 1 - percentage
                tvBackground.alpha = percentage
            } else {
                tvBackground.alpha = 0F
            }
        } else {
            if (currentX <= halfWidth) {
                percentage = currentX / (halfWidth)
                tvAboveBG.alpha = percentage
                tvBackground.alpha = 1 - percentage
            } else {
                tvBackground.alpha = 0F
            }
        }
        view.updateDragShadow(DragShadowBuilder(view))
    }

    fun updateNewItem(term: Term) {
        tvFront.text = term.term
        tvBack.text = term.definition
        tvBackground.text = term.term
        view.updateDragShadow(DragShadowBuilder(view))
    }

    override fun onProvideShadowMetrics(outShadowSize: Point?, outShadowTouchPoint: Point?) {

        super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint)
    }
}