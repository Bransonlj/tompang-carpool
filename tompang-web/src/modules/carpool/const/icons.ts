import driverGreen from "@/assets/maker-icons/drivergreen.svg";
import driverOrange from "@/assets/maker-icons/driverorange.svg";
import rider1Green from "@/assets/maker-icons/1green.svg";
import rider2Green from "@/assets/maker-icons/2green.svg";
import rider3Green from "@/assets/maker-icons/3green.svg";
import rider4Green from "@/assets/maker-icons/4green.svg";
import rider5Green from "@/assets/maker-icons/5green.svg";
import rider5plusGreen from "@/assets/maker-icons/5+green.svg";
import rider1Orange from "@/assets/maker-icons/1orange.svg";
import rider2Orange from "@/assets/maker-icons/2orange.svg";
import rider3Orange from "@/assets/maker-icons/3orange.svg";
import rider4Orange from "@/assets/maker-icons/4orange.svg";
import rider5Orange from "@/assets/maker-icons/5orange.svg";
import rider5plusOrange from "@/assets/maker-icons/5+orange.svg";
import { icon, type IconOptions } from "leaflet";
import markerShadow from "leaflet/dist/images/marker-shadow.png";

const iconOptions: Omit<IconOptions, "iconUrl"> = {
  shadowUrl: markerShadow,
  iconSize: [41, 41],      // size of the icon
  iconAnchor: [22, 42],    // point of the icon which corresponds to marker's location
  popupAnchor: [0, -32],   // point where popup opens
}

export const MarkerIcons = {
  driverOrigin: icon({
    iconUrl: driverGreen,
    ...iconOptions,
  }),
  driverDestination: icon({
    iconUrl: driverOrange,
    ...iconOptions,
  }),
  rider1Origin: icon({
    iconUrl: rider1Green,
    ...iconOptions,
  }),
  rider2Origin: icon({
    iconUrl: rider2Green,
    ...iconOptions,
  }),
  rider3Origin: icon({
    iconUrl: rider3Green,
    ...iconOptions,
  }),
  rider4Origin: icon({
    iconUrl: rider4Green,
    ...iconOptions,
  }),
  rider5Origin: icon({
    iconUrl: rider5Green,
    ...iconOptions,
  }),
  rider5plusOrigin: icon({
    iconUrl: rider5plusGreen,
    ...iconOptions,
  }),
  rider1Destination: icon({
    iconUrl: rider1Orange,
    ...iconOptions,
  }),
  rider2Destination: icon({
    iconUrl: rider2Orange,
    ...iconOptions,
  }),
    rider3Destination: icon({
    iconUrl: rider3Orange,
    ...iconOptions,
  }),
    rider4Destination: icon({
    iconUrl: rider4Orange,
    ...iconOptions,
  }),
    rider5Destination: icon({
    iconUrl: rider5Orange,
    ...iconOptions,
  }),
    rider5plusDestination: icon({
    iconUrl: rider5plusOrange,
    ...iconOptions,
  }),
};