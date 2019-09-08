layui.use(['form','layer','laydate','upload'], function(){
    var laydate = layui.laydate;
    var form = layui.form;
    var layer=layui.layer;
    var upload = layui.upload;

    var tableName="entityName-yangrui";

    initSelect();
    function initSelect() {
        selectCompoments-yangrui
    }
    form.on('submit(commit)', function(data){
        console.log(data.field);
        console.log(data.form)
        var commitData=data.field;
        //进行保存操作
        var commit={
            entityName:tableName,
            data:commitData
        }

        apiClient.insert(commit,function (data) {
            layer.msg("增加成功")
            var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
            parent.layer.close(index); //再执行关闭
            parent.layui.table.reload('data',{
                curr:parent.location.hash.replace('#!fenye=', '')
            })
        });
        return false;
    })
    timerCompoments-yangrui

    uploadCompoments-yangrui
});