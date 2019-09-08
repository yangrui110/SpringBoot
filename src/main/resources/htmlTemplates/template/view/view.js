layui.use(['form','layer','laydate','upload'], function() {
    var form = layui.form;
    var laydate = layui.laydate;
    var upload = layui.upload;
    var layer=layui.layer;
    var entityName = "entityName-yangrui";
    var viewName='aliasName-yangrui';
    var param = paramUtil.getQueryString("param");
    var uriComponent = JSON.parse(decodeURIComponent(param));
    var editorData={};
    console.log(uriComponent)
    var conditionList=[];
    for(var k in uriComponent){
        var ob={};
        ob.left=k;
        ob.right=uriComponent[k];
        conditionList.push(ob);
    }
    //从服务器获取数据
    apiClient.findAllNoPage({
        entityName:viewName,
        condition:{
            conditionList:conditionList,
            combine:"and"
        }
    },function (data) {
        if(data.data.length>0)
            editorData=data.data[0];
        form.val("viewForm",editorData);
    })

    initSelect();
    function initSelect() {
        selectCheckedCompoments-yangrui
    }

})