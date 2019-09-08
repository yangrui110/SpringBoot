layui.use(['form','layer','laydate','upload'], function(){
    var laydate = layui.laydate;
    var form = layui.form;
    var layer=layui.layer;
    var upload = layui.upload;

    var entityName="entityName-yangrui";
    var param =paramUtil.getQueryString("param");
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

    initSelect();
    function initSelect() {
        selectCheckedCompoments-yangrui
    }

    //从服务器获取数据
    apiClient.findAllNoPage({
        entityName:entityName,
        condition:{
            conditionList:conditionList,
            combine:"and"
        }
    },function (data) {
        if(data.data.length>0)
            editorData=data.data[0];
        form.val("editorForm",editorData);
    })

    form.on('submit(commit)', function(data){
        apiClient.update({
            entityName: entityName,
            condition:{
                conditionList:conditionList,
                combine:"and"
            },
            data: data.field
        },function (data) {
            layer.msg("修改成功")
            var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
            parent.layer.close(index); //再执行关闭
            parent.layui.table.reload('data',{
                curr:parent.location.hash.replace('#!fenye=', '')
            })
        })
        return false;
    })
    timerCompoments-yangrui

    uploadCompoments-yangrui
})