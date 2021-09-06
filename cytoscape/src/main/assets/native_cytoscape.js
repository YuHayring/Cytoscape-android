var cy = cytoscape({
container: document.getElementById('cytoscape'), // container to render in
style: [{ selector: 'node',
        css: {'content': 'data('+ getQueryVariable("nodeContentField") +')'}
    }],
pixelRatio: 0.7,
});
bridge.registerAsyn("cy", cy)
bridge.registerAsyn("console",console)
bridge.call("onCytoscapeLoaded","")
bridge.register("init",{
  setNodeContent:function(name){
    //cy.nodes().css({content:"data(" +name + ")"})
    console.log("nodecontent:" + name)
  },
})

cy.on('tap', 'node', function(event){
  bridge.call("onNodeClick", event.target.id(), function () {})
});

cy.on('tap', 'edge', function(event){
  bridge.call("onEdgeClick", event.target.id(), function () {})
});

cy.on('taphold', 'node', function(event){
  bridge.call("onNodeLongClick", event.target.id(), function () {})
});

cy.on('taphold', 'edge', function(event){
  bridge.call("onEdgeLongClick", event.target.id(), function () {})
});


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

let options = {
  name: 'grid',

  fit: true, // whether to fit the viewport to the graph
  padding: 30, // padding used on fit
  avoidOverlap: true, // prevents node overlap, may overflow boundingBox if not enough space
  avoidOverlapPadding: 10, // extra spacing around nodes when avoidOverlap: true
};

cy.layout( options );
