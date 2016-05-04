
\d .xml

// rtm -> Ref/Business time stamp
// ptm -> Published timestamp
// isUpdate -> Flag indicating if this is an update message
// <n> -> Namespace
// <v> -> Value

.xml.build_ns:{[ccy;ohlc;bidask;ns_template]
    :ssr[
        ssr[
            ssr[
                ns_template;
                "CCYPAIR"; upper ccy];
            "OHLC"; upper ohlc];
        "BIDASK"; upper bidask];
    };

.xml.build_child: {[x;y;record;ns_template]
    child_template:"<d><n>NS_PLACEHOLDER</n><v>VAL_PLACEHOLDER</v></d>";
    x:string x;
    y:string y;
    col:`$y,x;
    
    ns:.xml.build_ns[string record[`sym];x;y;ns_template];
    val: record[col];
    
    result:$[
        0n~val;
        "";
        ssr[ssr[child_template;"NS_PLACEHOLDER";ns];"VAL_PLACEHOLDER";string val]
        ];
    result
    };       

/ format RTM and PTM to the correct format
.xml.format_datetime:{[datetime]
    day:ssr[string `date$datetime;".";"/"];
    time:ssr[string `time$datetime;".";" "]; // replace the milisecond part separator by a space
    result:day," ",time;
    result
    };

.xml.get_datetime:{[data]
    r:" " vs data;
    rdt:"D"$r[0];
    rtm:"T"$r[1],".",r[2];
    resdt: "Z"$(string rdt),"T",(string rtm);
    :resdt
    };
              
.xml.create:{[records;is_update]
    parent_template:"<?xml version=\"1.0\" encoding=\"UTF-8\"?><md rtm=\"RTM_PLACEHOLDER\" ptm=\"PTM_PLACEHOLDER\" isUpdate=\"IS_UPD_PLACEHOLDER\">CHILDREN_PLACEHOLDER</md>";
    
    g:{[records;symbol]
        OHLC:`Open`High`Low`Close;
        BIDASK:`bid`mid`ask;
        record:$[-11h~type records[`sym];records;exec from records where sym=symbol];
        f:.xml.build_child[;;record;"FX.SPOT.CCYPAIR.OHLC.BIDASK.INTRADAY.CITI"];
        :raze/[(f'[OHLC])'[BIDASK]];
    }[records;];
    
    symList: exec sym from records;
    children:raze/[g each symList];    
    rtm:exec last time from records;
    
    is_update:$[is_update;"true";"false"];
    parent:ssr[
            ssr[
             ssr[
                ssr[
                    parent_template;
                    "CHILDREN_PLACEHOLDER";$[""~children;"";children]];
                "IS_UPD_PLACEHOLDER";is_update];
             "RTM_PLACEHOLDER"; .xml.format_datetime[rtm]];
          "PTM_PLACEHOLDER"; .xml.format_datetime[.z.z] // published datetime is assumed to be the time of creating this XML
        ];
    :parent
    };

.xml.create_snapshot:{[records]
    parent_template:"<?xml version=\"1.0\" encoding=\"UTF-8\"?><md rtm=\"RTM_PLACEHOLDER\" ptm=\"PTM_PLACEHOLDER\">CHILDREN_PLACEHOLDER</md>";
    
    symList: exec sym from records;
    closeTime: exec last time from records;
    
    g:{[records;symbol]
        OHLC: enlist `Close;
        BIDASK:`bid`mid`ask;
        
        f:.xml.build_child[;;exec from records where sym=symbol;"FX.SPOT.CCYPAIR.BIDASK.EOD.CITI"];
        :raze/[(f'[OHLC])'[BIDASK]];
    }[records;];
    
    children:raze/[g each symList];
    parent:ssr[
             ssr[
                ssr[
                    parent_template;
                    "CHILDREN_PLACEHOLDER";$[""~children;"";children]];
             "RTM_PLACEHOLDER"; .xml.format_datetime[closeTime]];
          "PTM_PLACEHOLDER"; .xml.format_datetime[.z.z] // published datetime is assumed to be the time of creating this XML
        ];
    parent    
    };

.xml.create_snap:{[records;is_update] / records:.xml.create_snap[publishingData;0b]
    parent_template:"<?xml version=\"1.0\" encoding=\"UTF-8\"?><md rtm=\"RTM_PLACEHOLDER\" ptm=\"PTM_PLACEHOLDER\" isUpdate=\"IS_UPD_PLACEHOLDER\">CHILDREN_PLACEHOLDER</md>";
    g:{[records;symbol]
        OHLC: enlist `$"";
        BIDASK:`bid`mid`ask;
        record:$[-9h~type records[`bid];records;exec from records where (sym=symbol)];
        f:.xml.build_child[;;record;"RATES.DEPOSIT.CCYPAIR.BIDASK.INTRADAY.CITI"];
        :raze/[(f'[OHLC])'[BIDASK]];
    }[records;];
    
    symList: exec sym from records;
    children:raze/[g each symList];    
    rtm:exec last timestamp from records;
    
    is_update:$[is_update;"true";"false"];
    parent:ssr[
            ssr[
             ssr[
                ssr[
                    parent_template;
                    "CHILDREN_PLACEHOLDER";$[""~children;"";children]];
                "IS_UPD_PLACEHOLDER";is_update];
             "RTM_PLACEHOLDER"; .xml.format_datetime[rtm]];
          "PTM_PLACEHOLDER"; .xml.format_datetime[.z.z] // published datetime is assumed to be the time of creating this XML
        ];
    :parent
    };        