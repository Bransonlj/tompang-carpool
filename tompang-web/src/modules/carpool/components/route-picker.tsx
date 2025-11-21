import Button from '@mui/material/Button';
import { LatLng, type LeafletEventHandlerFnMap, type Marker as  MarkerType, type Map as LeafletMap } from 'leaflet';
import { useRef, useState } from 'react';
import { MapContainer, Marker, TileLayer, useMapEvents } from 'react-leaflet';
import { SINGAPORE } from '../const';
import Divider from '@mui/material/Divider';

interface MoveableMarkerProps {
  moveable?: boolean;
  value?: LatLng;
  defaultValue?: LatLng
  onChange?: (location: LatLng) => void;
}

function MoveableMarker({
  moveable=false,
  value,
  defaultValue,
  onChange
}: MoveableMarkerProps) {
  const [position, setPosition] = useState<LatLng>(defaultValue ?? SINGAPORE)
  const markerRef = useRef<MarkerType>(null)
  const eventHandlers: LeafletEventHandlerFnMap = {
      dragend() {
        const marker = markerRef.current
        if (marker != null) {
          onChange?.(marker.getLatLng());
          setPosition(marker.getLatLng());
        }
      },
    };

  // Handle map clicks if draggable
  useMapEvents({
    click(e) {
      if (moveable) {
        setPosition(e.latlng);
        onChange?.(e.latlng);
      }
    },
  });

  return (
    <Marker
      draggable={moveable}
      eventHandlers={eventHandlers}
      position={value ?? position}
      ref={markerRef}>
    </Marker>
  )
}

type Mode = "origin" | "destination";

interface ModeSelectorProps {
  disabled?: boolean;
  value: LatLng;
  label: string;
  onClick: () => void;
}

function ModeSelector({
  disabled,
  value,
  label,
  onClick
}: ModeSelectorProps) {
  return (
    <Button disabled={disabled} onClick={onClick} className='flex flex-col w-32'>
      <h2>{label}</h2>
      <p className='text-sm font-thin'>{value.lat.toPrecision(3)} {value.lng.toPrecision(3)}</p>
    </Button>
  )
}

interface RoutePickerProps {
  origin?: LatLng;
  onOriginChange?: (location: LatLng) => void;
  destination?: LatLng;
  onDestinationChange?: (location: LatLng) => void;
}

export default function RoutePicker({
  origin: controlledOrigin,
  destination: controlledDestination,
  onOriginChange,
  onDestinationChange,
}: RoutePickerProps) {
  const [uncontrolledOrigin, setUncontrolledOrigin] = useState<LatLng>(SINGAPORE);
  const [uncontrolledDestination, setUncontrolledDestination] = useState<LatLng>(SINGAPORE);

  const origin = controlledOrigin ?? uncontrolledOrigin;
  const setOrigin = onOriginChange ?? setUncontrolledOrigin;
  const destination = controlledDestination ?? uncontrolledDestination;
  const setDestination = onDestinationChange ?? setUncontrolledDestination;

  const [mode, setMode] = useState<Mode>("origin");
  const mapRef = useRef<LeafletMap | null>(null);

  function handleSelect(mode: Mode) {
    setMode(mode);
    if (mapRef.current) {
      mapRef.current.setView(mode === "origin" ? origin : destination, mapRef.current.getZoom(), { animate: true });
    }
  }

  return (
      <div className='flex items-start gap-2'>
        <MapContainer center={SINGAPORE} zoom={12} scrollWheelZoom={true} className='w-80 h-64' ref={mapRef}>
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          <MoveableMarker moveable={mode === "origin"} value={origin} onChange={setOrigin} />
          <MoveableMarker moveable={mode === "destination"} value={destination} onChange={setDestination} />
        </MapContainer>
        <Divider flexItem orientation="vertical" />
        <ModeSelector disabled={mode === "origin"} onClick={() => handleSelect("origin")} label='Origin' value={origin}/>
        <ModeSelector disabled={mode === "destination"} onClick={() => handleSelect("destination")} label='Destination' value={destination}/>
    </div>
  )
}