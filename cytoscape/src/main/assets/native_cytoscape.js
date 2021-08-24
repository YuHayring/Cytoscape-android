var cy = cytoscape({
container: document.getElementById('cytoscape'), // container to render in
style: [{ selector: 'node',
        css: {'content': 'data('+ getQueryVariable("nodeContentField") +')'}
    }],
pixelRatio: 0.7,
});
dsBridge.registerAsyn("cy", cy)
dsBridge.registerAsyn("console",console)
dsBridge.call("onCytoscapeLoaded")
dsBridge.register("init",{
  setNodeContent:function(name){
    //cy.nodes().css({content:"data(" +name + ")"})
    console.log("nodecontent:" + name)
  },
})


function getQueryVariable(variable)
{
       var query = window.location.search.substring(1);
       var vars = query.split("&");
       for (var i=0;i<vars.length;i++) {
               var pair = vars[i].split("=");
               if(pair[0] == variable){return pair[1];}
       }
       return(false);
}

