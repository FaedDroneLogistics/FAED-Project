	$.getScript( "js/poi_moon.js", function( data, textStatus, jqxhr ) {
	  //console.log( data ); // Data returned
	  //console.log( textStatus ); // Success
	 // console.log( jqxhr.status ); // 200
	 // console.log( "Load was performed." );
	});


var moonTypeOptions = {
    getTileUrl: function(coord, zoom) {
        var normalizedCoord = getNormalizedCoord(coord, zoom);
        if (!normalizedCoord) {
          return null;
        }
        var bound = Math.pow(2, zoom);
        return 'http://mw1.google.com/mw-planetary/lunar/lunarmaps_v1/clem_bw' +
            '/' + zoom + '/' + normalizedCoord.x + '/' +
            (bound - normalizedCoord.y - 1) + '.jpg';
    },
    tileSize: new google.maps.Size(256, 256),
    maxZoom: 9,
    minZoom: 0,
    radius: 1738000,
    name: 'Moon'
  };

  var moonMapType = new google.maps.ImageMapType(moonTypeOptions);
  


gmarkers = [];

    //var map;

    function map_moon() {


    var myLatlng = new google.maps.LatLng(0, 0);
    var mapOptions = {
      center: myLatlng,
      zoom: 3,
      streetViewControl: false,
      mapTypeControlOptions: {
        mapTypeIds: ['moon']
      }
    };
     var map = new google.maps.Map(document.getElementById('map-canvas'),
        mapOptions);
    map.mapTypes.set('moon', moonMapType);
    map.setMapTypeId('moon');

var infowindow = new google.maps.InfoWindow();


function createMarker(latlng, html) {
    var position = new google.maps.LatLng(locations[i][1], locations[i][2]);
    var marker = new google.maps.Marker({
        position: position,
        map: map
    });

    google.maps.event.addListener(marker, 'click', function() {
        infowindow.setContent(html);
        infowindow.open(map, marker);
    });
    return marker;
}

for (var i = 0; i < locations.length; i++) {
    gmarkers[locations[i][0]] =
    createMarker(new google.maps.LatLng(locations[i][1], locations[i][2]), "<h1>" + locations[i][0] + "</h1>" + "<br>" + locations[i][3]);
}
    }



 // Normalizes the coords that tiles repeat across the x axis (horizontally)
  // like the standard Google map tiles.
  function getNormalizedCoord(coord, zoom) {
    var y = coord.y;
    var x = coord.x;

    // tile range in one direction range is dependent on zoom level
    // 0 = 1 tile, 1 = 2 tiles, 2 = 4 tiles, 3 = 8 tiles, etc
    var tileRange = 1 << zoom;

    // don't repeat across y-axis (vertically)
    if (y < 0 || y >= tileRange) {
      return null;
    }

    // repeat across x-axis
    if (x < 0 || x >= tileRange) {
      x = (x % tileRange + tileRange) % tileRange;
    }

    return {
      x: x,
      y: y
    };
  }



    //google.maps.event.addDomListener(window, 'load', initialize);

