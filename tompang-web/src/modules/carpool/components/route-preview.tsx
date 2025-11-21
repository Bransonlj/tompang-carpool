import { latLngBounds, type LatLngBoundsExpression, type LatLngExpression } from "leaflet"
import { MapContainer, Marker, TileLayer, useMap } from "react-leaflet";
import { MarkerIcons, SINGAPORE } from "../const";
import { useEffect, useMemo } from "react";

type StartEndLocation = [LatLngExpression, LatLngExpression];

interface RoutePreviewProps {
  primaryStartEnd: StartEndLocation;
  secondaryStartEnds?: StartEndLocation[];
  className?: string;
}

function getRiderIcon(index: number, origin: boolean) {
  switch (index) {
    case 0: return origin ? MarkerIcons.rider1Origin : MarkerIcons.rider1Destination;
    case 1: return origin ? MarkerIcons.rider2Origin : MarkerIcons.rider2Destination;
    case 2: return origin ? MarkerIcons.rider3Origin : MarkerIcons.rider3Destination;
    case 3: return origin ? MarkerIcons.rider4Origin : MarkerIcons.rider4Destination;
    case 4: return origin ? MarkerIcons.rider5Origin : MarkerIcons.rider5Destination;
    default: return origin ? MarkerIcons.rider5plusOrigin : MarkerIcons.rider5plusDestination;
  }
}

const FitBounds = ({
  bounds,
}: {
  bounds: LatLngBoundsExpression;
}) => {
  const map = useMap();

  useEffect(() => {
    map.fitBounds(bounds, { padding: [50, 50] });
  }, [map, bounds]);

  return null;
};

export default function RoutePreview({
  primaryStartEnd,
  secondaryStartEnds=[],
  className,
}: RoutePreviewProps) {
  const markerBounds = useMemo(() => 
    latLngBounds([...primaryStartEnd, ...secondaryStartEnds.flatMap(x => x)]), 
    [primaryStartEnd, secondaryStartEnds]
  );

  return (
    <MapContainer center={SINGAPORE} zoom={13} scrollWheelZoom={false} className={`${className}`}>
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      <Marker position={primaryStartEnd[0]} icon={MarkerIcons.driverOrigin} />
      <Marker position={primaryStartEnd[1]} icon={MarkerIcons.driverDestination}/>
      {
        secondaryStartEnds.map((startEnd, index) => (
          <>
            <Marker position={startEnd[0]} icon={getRiderIcon(index, true)} />
            <Marker position={startEnd[1]} icon={getRiderIcon(index, false)}/>
          </>
        ))
      }
      <FitBounds bounds={markerBounds} />
    </MapContainer>
  )
}