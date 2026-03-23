declare module 'vue-masonry-css' {
  import { DefineComponent } from 'vue';

  interface MasonryProps {
    cols?: number | { 
      default?: number;
      [key: number]: number;
    };
    gap?: number;
    gutter?: number;
    rtl?: boolean;
  }

  const Masonry: DefineComponent<MasonryProps>;

  export default Masonry;
}