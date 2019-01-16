package com.xue;


/**
 * 工作流类型
 *
 * @author 张喜龙
 * @create 2018-08-30 21:04
 **/
public enum WorkflowTypeEnums {

    //采购审批
    RPOCUREMENT("采购申请", "DELUKE-PURCHASE"),

    //采购出款审批
    RPOCUREMENT_PAYMENT("采购出款", "DELUKE-PURCHASE-COST-ENSURE"),

    //车辆采购确认入库审批
    RPOCUREMENT_INSTOCK("车辆采购确认入库", "DELUKE-CAR-PUT-IN"),

    //拍卖处置申请
    APPLY_AUCTION("拍卖申请", "DELUKE-AUCTION-HANDLE"),

    //降价成交审批
    REDUCE_PRICE_DEAL("拍卖成交", "DELUKE-LOWER-RESERVE-PRICE"),

    //改价审批
    APPLY_ALTER_PRICE("拍卖改价申请", "DELUKE-MODIFY-AUCTION-ORDER-PRICE"),

    //拍卖车辆确认出库审批
    AUCTION_CAR_OUTSTOCK("拍卖车辆确认出库", "DELUKE-AUCTION-CAR-TAKE-OUT"),

    //指定成交
    ASSIGN("指定成交申请", "DELUKE-ASSIGN-BARGAIN-SPONSOR"),

    //指定成交出库审批
    ASSIGN_DEAL_OUTSTOCK("指定成交出库", "DELUKE-ASSIGN-BARGAIN-CAR-TAKE-OUT");

//    DELUKE_SALE("销售","DELUKE-SALE"),
//
//    DELUKE_DEFEAT("战败客户", "DELUKE-DEFEAT"),
//
//    DELUKE_REIMBURSEMENT("费用报销","DELUKE-REIMBURSEMENT");

    private String desc;
    private String remoteServiceCode;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (WorkflowTypeEnums workflowTypeEnums:WorkflowTypeEnums.values()) {
            stringBuilder.append("(")
                    .append(workflowTypeEnums.getDesc())
                    .append(":")
                    .append(workflowTypeEnums.ordinal())
                    .append(")");
        }

        return stringBuilder.toString();
    }

    public static WorkflowTypeEnums of(int ordinal) {
        for (WorkflowTypeEnums workflowTypeEnums:WorkflowTypeEnums.values()) {
            if(workflowTypeEnums.ordinal() != ordinal){
                continue;
            }
            return workflowTypeEnums;
        }
        return null;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRemoteServiceCode() {
        return remoteServiceCode;
    }

    public void setRemoteServiceCode(String remoteServiceCode) {
        this.remoteServiceCode = remoteServiceCode;
    }

    WorkflowTypeEnums(String desc, String remoteServiceCode) {
        this.desc = desc;
        this.remoteServiceCode = remoteServiceCode;
    }

    public static WorkflowTypeEnums getEnumByOrdinal(Integer ordinal){
        for(WorkflowTypeEnums workflowTypeEnums : WorkflowTypeEnums.values()){
            if(ordinal.equals(workflowTypeEnums.ordinal())){
                return workflowTypeEnums;
            }
        }
        return null;
    }
}
