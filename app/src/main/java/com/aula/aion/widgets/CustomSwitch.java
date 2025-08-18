package com.aula.aion.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat; // Para carregar drawables
import com.aula.aion.R; // Certifique-se que o R aponta para o seu projeto

public class CustomSwitch extends FrameLayout {
    private View thumb;
    private boolean isChecked = false;
    private OnCheckedChangeListener listener;

    // Drawables para o estado do trilho
    private Drawable trackOnDrawable;
    private Drawable trackOffDrawable;
    // Drawables para o estado do thumb
    private Drawable thumbOnDrawable;
    private Drawable thumbOffDrawable;

    // Constantes para o padding do thumb dentro do track (ajuste conforme seu design)
    private static final int THUMB_HORIZONTAL_PADDING_DP = 8; // Espaçamento do thumb para as bordas do track
    private static final int OFF_STATE_EXTRA_OFFSET_DP = -8; // Aumento extra para o trilho quando desligado
    private static final int ON_STATE_EXTRA_OFFSET_DP = -5; // Aumento extra para o trilho quando desligado
    public CustomSwitch(Context context) {
        super(context);
        init(context);
    }

    public CustomSwitch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomSwitch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        // Infla o layout que contém o thumb. O CustomSwitch VAI SER o trilho.
        LayoutInflater.from(context).inflate(R.layout.view_custom_switch, this, true);
        thumb = findViewById(R.id.switch_thumb);

        // Carrega os Drawables (aqui você pode simplificar para cores fixas, por exemplo)
        // Você precisará desses drawables criados em res/drawable/
        trackOnDrawable = ContextCompat.getDrawable(context, R.drawable.bg_switch_on);
        trackOffDrawable = ContextCompat.getDrawable(context, R.drawable.bg_switch_off);
        thumbOnDrawable = ContextCompat.getDrawable(context, R.drawable.thumb_switch);
        thumbOffDrawable = ContextCompat.getDrawable(context, R.drawable.thumb_switch);

        setClickable(true);
        setFocusable(true);

        setOnClickListener(v -> toggle());

        // A posição inicial do thumb será atualizada em onSizeChanged()
        // e o background inicial também será definido lá.
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Quando o tamanho da view é conhecido, atualizamos o estado visual
        updateVisualState(false); // Não animar na inicialização
    }

    private void toggle() {
        isChecked = !isChecked;
        updateVisualState(true); // Animar ao clicar

        if (listener != null) {
            listener.onCheckedChanged(isChecked);
        }
    }

    // Método central para atualizar tanto a posição do thumb quanto o background
    private void updateVisualState(boolean animate)
    {
        // 1. Atualizar a posição do Thumb
        if (thumb != null && getWidth() > 0 && thumb.getWidth() > 0) {
            int thumbPaddingPx = dpToPx(THUMB_HORIZONTAL_PADDING_DP);
            int targetX;

            if (isChecked) {
                // Se estiver checado (ligado), a posição é a mesma: no final do trilho, menos o padding
                targetX = getWidth() - thumb.getWidth() - thumbPaddingPx - dpToPx(ON_STATE_EXTRA_OFFSET_DP);
            } else {
                targetX = thumbPaddingPx + dpToPx(OFF_STATE_EXTRA_OFFSET_DP);
            }

            if (animate) {
                thumb.animate()
                        .translationX(targetX)
                        .setDuration(200)
                        .start();
            } else {
                thumb.setTranslationX(targetX);
            }
        }

        // 2. Atualizar o Background do Trilho (CustomSwitch)
        if (isChecked && trackOnDrawable != null) {
            setBackground(trackOnDrawable);
        } else if (!isChecked && trackOffDrawable != null) {
            setBackground(trackOffDrawable);
        }

        // 3. Atualizar o Background do Thumb
        if (isChecked && thumbOnDrawable != null) {
            thumb.setBackground(thumbOnDrawable);
        } else if (!isChecked && thumbOffDrawable != null) {
            thumb.setBackground(thumbOffDrawable);
        }

        // Isso forçará o Android a redesenhar a view, aplicando os drawables corretos.
        invalidate();
    }

    public void setChecked(boolean checked) {
        if (this.isChecked != checked) {
            this.isChecked = checked;
            updateVisualState(false); // Define o estado sem animação
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(boolean isChecked);
    }

    // Este método é essencial para que os drawables saibam o estado "checked"
    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked) {
            mergeDrawableStates(drawableState, new int[]{android.R.attr.state_checked});
        }
        return drawableState;
    }

    private int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}