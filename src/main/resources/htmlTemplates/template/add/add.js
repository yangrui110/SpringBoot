layui.use('form', function(){
    var form = layui.form;

    var addData={};

    var tableName="entityName-yangrui";
    //初始化vue.js的数据绑定
    var app4 = new Vue({
        el: '#editorForm',
        data: {
            addData:addData
        }
    })
    form.on('submit(commit)', function(data){
        console.log(data.field);
        //进行保存操作
        var commit={
            entityName:tableName,
            data:addData
        };
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
});