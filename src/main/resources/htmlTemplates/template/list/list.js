layui.use(['table','form','layer','laydate','upload'], function(){
    var table = layui.table;
    var laydate = layui.laydate;
    var form = layui.form;
    var upload = layui.upload;
    var layer=layui.layer;
    var viewName="aliasName-yangrui";
    var entityName = "entityName-yangrui";
    var skin="innerIframe";
    var queryData= {
        "entityName": viewName,
        "data": {"conditionList":[]}
        orderBy-yangrui
    }
    var pk={};
    //给pk赋值
    apiClient.getPK({viewEntityName:viewName,entityName:entityName},function (data) {
        pk = data.data;
        console.log(data);
    })

    initSelect();
    function initSelect() {
        selectCompoments-yangrui
    }
    //第一个实例
    var tableIns=table.render({
        elem: '#data'
        ,height: 312
        ,url: apiClient.getUrl()+'/common/findAll' //数据接口
        ,page: {
            curr:location.hash.replace('#!fenye=', '')
            ,hash: 'fenye'
        } //开启分页
        ,method: "post"
        ,limit: 2
        ,contentType: 'application/json'
        ,where:queryData
        ,request: {
            pageName: 'start' //页码的参数名称，默认：page
            ,limitName: 'end' //每页数据量的参数名，默认：limit
        }
        ,response: {
            statusCode: 200
        }
        ,done:function (res,curr,count) {
            console.log(res.data.length+"----"+curr+"----"+count);
            if(res.data.length==0&&count!=0){
                table.reload("data",{
                    page:{
                        curr:curr-1
                        ,hash: 'fenye'
                    }
                });
            }
        }
        ,cols: [[ //表头
            {type:'checkbox'}
            fields-yangrui
            ,{title: '操作',align: 'center',toolbar: '#operator'}
        ]]

    });


    //表单提交
    form.on('submit(search)', function(data){
        console.log(data.elem) //被执行事件的元素DOM对象，一般为button对象
        console.log(data.form) //被执行提交的form对象，一般在存在form标签时才会返回
        console.log(data.field) //当前容器的全部表单字段，名值对形式：{name: value}
        queryData.data.conditionList=[];
        for(var k in data.field){
            if(data.field[k]!='')
                queryData.data.conditionList.push({left:k,right:data.field[k],operator:'like'});
        }
        tableIns.reload('data',{
            page:{
                curr:1
            }
        });
        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });

    var $ = layui.$, active = {
        addData:function () {
            //打开增加界面
            console.log("add")
            layer.open({
                type:2,
                title:"新增用户",
                content:"../aliasName-yangrui-add/aliasName-yangrui-add.html",
                area:['60%'],
                skin:skin
            })
            console.log("xxxx");
        },
        delSelect:function () {
            var checkStatus = table.checkStatus('data')
                ,data = checkStatus.data;
            if(data.length<0){
                layer.msg("请选择需要删除的数据行");
                return ;
            }else{
                var result=[];
                for(var v of data){
                    var r1={};
                    for(var k in pk){
                        r1[pk[k]]=v[k];
                    }
                    result.push(r1);
                }
                apiClient.delSelect({
                    entityName:viewName,
                    datas:result
                },function (data) {
                    layer.msg("删除成功");
                    table.reload('data');
                })
            }
        }
    }
    $('.dealTable .dataType').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });

    //监听工具条
    table.on('tool(dataFilter)', function(obj){
        var data = obj.data;
        var parseData={};
        for(var key in pk){
            if(data[key]!=null)
                parseData[key]=data[key];
        }
        if(obj.event === 'detail'){
            //打开详情界面
            layer.open({
                type:2,
                title:"详情",
                content:"../aliasName-yangrui-view/aliasName-yangrui-view.html?param="+encodeURIComponent(JSON.stringify(parseData)),
                area:['60%'],
                skin:skin
            })
        } else if(obj.event === 'del'){
            layer.confirm('删除该记录值？', function(index){
                var conditionList=[];
                for(var k in parseData){
                    var ob={};
                    ob.left=pk[k];
                    ob.right=parseData[k];
                    conditionList.push(ob);
                }
                apiClient.delete({
                    entityName:entityName,
                    condition:{
                        conditionList:conditionList
                    }},function (data) {
                    layer.msg("删除成功");
                    obj.del();
                    layer.close(index);
                    //重新加载表格数据
                    console.log(location.hash.replace('#!fenye=', ''));
                    table.reload('data');
                })

            });
        } else if(obj.event === 'edit'){
            //打开编辑界面
            //构造参数
            var param={};
            for(var k in parseData){
                param[pk[k]]=parseData[k];
            }
            layer.open({
                type:2,
                title:"修改用户",
                content:"../aliasName-yangrui-edit/aliasName-yangrui-editor.html?param="+encodeURIComponent(JSON.stringify(param)),
                area:['60%'],
                skin:skin
            })
        }
    });
    timerCompoments-yangrui
});