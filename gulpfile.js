const gulp = require('gulp');
const ts = require('gulp-typescript');
const map = require('vinyl-map');
let log = require('fancy-log');


const modify = map((contents, filename) => {
    contents = contents.toString();
    contents = contents.replace("exports.__esModule = true;", "");
    return contents;
});

function tsscripts() {
    return gulp.src('frontend/js/**/*.ts')
        .pipe(ts({
            declaration: true
        }))
        .pipe(gulp.dest('frontend/build/js'));
}

function jsscripts() {
    return gulp.src('frontend/js/**/*.js')
        .pipe(modify)
        .pipe(gulp.dest('frontend/build/js'));
}

function css() {
    return gulp.src('frontend/css/**/*.css')
        .pipe(gulp.dest('frontend/build/css'))
}

function html() {
    return gulp.src('frontend/**/*.html')
        .pipe(gulp.dest('frontend/build/'))
}

gulp.task('build', function() {
    tsscripts();
    jsscripts();
    let result1 = css();
    html();
    log("Hi");
    return result1;
});