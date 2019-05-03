package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.adapter.RetailItemsChoicesAdapter;
import com.carecloud.carepay.practice.library.payments.adapter.RetailItemsOptionsAdapter;
import com.carecloud.carepay.practice.library.payments.interfaces.AddPaymentItemCallback;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.retail.models.RetailItemDto;
import com.carecloud.carepaylibray.retail.models.RetailItemOptionChoiceDto;
import com.carecloud.carepaylibray.retail.models.RetailItemOptionDto;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.Map;
import java.util.TreeMap;

public class RetailItemOptionsFragment extends BaseDialogFragment implements RetailItemsChoicesAdapter.RetailChoiceSelectedListener {
    private static final int MINIMUM_QTY = 1;

    private RetailItemDto retailItemDto;
    private AddPaymentItemCallback callback;

    private TextView priceTextView;
    private TextView quantityTextView;

    private int quantity = MINIMUM_QTY;
    private double totalValue;
    private double priceModification;
    private Map<Integer, RetailItemOptionChoiceDto> priceModificationMap = new TreeMap<>();

    public static RetailItemOptionsFragment newInstance(RetailItemDto retailItemDto){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, retailItemDto);

        RetailItemOptionsFragment fragment = new RetailItemOptionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        Bundle args = getArguments();
        retailItemDto = DtoHelper.getConvertedDTO(RetailItemDto.class, args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_retail_item_options, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        setupToolbar(view);

        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        quantityTextView = view.findViewById(R.id.qty_text);
        quantityTextView.setText(String.valueOf(quantity));

        priceTextView = view.findViewById(R.id.price_text);
        loadDefaultOptions();
        calculateModifiedPrice();
        updatePrice();

        View addButton = view.findViewById(R.id.action_add);
        View subtractButton = view.findViewById(R.id.action_minus);
        addButton.setOnClickListener(qtyEditListener);
        subtractButton.setOnClickListener(qtyEditListener);

        View addRetailButton = view.findViewById(R.id.add_charge_button);
        addRetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.addRetailItemWithOptions(retailItemDto, quantity, priceModificationMap);//todo
                dismiss();
            }
        });

        RecyclerView optonsRecycler = view.findViewById(R.id.options_recycler);
        optonsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        setAdapter(optonsRecycler);
    }

    private void loadDefaultOptions(){
        for(int i=0; i<retailItemDto.getOptions().size(); i++){
            RetailItemOptionDto option = retailItemDto.getOptions().get(i);
            if(!option.getChoices().isEmpty()) {
                RetailItemOptionChoiceDto choice = option.getChoices().get(option.getDefaultChoice());
                priceModificationMap.put(i, choice);
            }
        }
    }

    private void updatePrice(){
        totalValue = SystemUtil.safeMultiply(quantity,
                SystemUtil.safeAdd(retailItemDto.getPrice(), priceModification));
        priceTextView.setText(StringUtil.getFormattedBalanceAmount(totalValue));
    }

    private void setupToolbar(View view){
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        TextView textView = toolbar.findViewById(R.id.toolbar_title);
        textView.setText(retailItemDto.getName());
    }

    private void setAdapter(RecyclerView optionsRecycler){
        RetailItemsOptionsAdapter adapter = new RetailItemsOptionsAdapter(getContext(), retailItemDto.getOptions(), this);
        optionsRecycler.setAdapter(adapter);
    }

    private View.OnClickListener qtyEditListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.action_add){
                quantity++;
            }else if(quantity > MINIMUM_QTY){
                quantity--;
            }
            quantityTextView.setText(String.valueOf(quantity));
            updatePrice();
        }
    };

    public void setCallback(AddPaymentItemCallback callback){
        this.callback = callback;
    }

    @Override
    public void onRetailChoiceSelected(RetailItemOptionChoiceDto choiceDto, int priority) {
        priceModificationMap.put(priority, choiceDto);
        calculateModifiedPrice();
    }

    private void calculateModifiedPrice(){
        double basePrice = retailItemDto.getPrice();
        for(RetailItemOptionChoiceDto choiceDto : priceModificationMap.values()){
            double modificationAmount = choiceDto.getPriceModify();
            if(modificationAmount != 0){
                switch (choiceDto.getPriceModifyType()){
                    case RetailItemOptionChoiceDto.MODIFIER_TYPE_PERCENT:
                        basePrice = basePrice + SystemUtil.safeMultiply(basePrice,
                                modificationAmount/100);
                        break;
                    case RetailItemOptionChoiceDto.MODIFIER_TYPE_AMOUNT:
                    default:
                        basePrice += modificationAmount;
                }
            }

            priceModification = SystemUtil.safeSubtract(basePrice, retailItemDto.getPrice());
            updatePrice();
        }
    }
}
