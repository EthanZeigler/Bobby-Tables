const { gulp, watch } = require('gulp');
const watch = require('gulp-watch')
var browserSync = require('browser-sync');


function js() {
    return gulp.src('./js/**/*.js')
        .pipe(gulp.dest('./build/js'));
}

function css() {
    return gulp.src('./css/**/*.css')
        .pipe(gulp.dest('./build/css'))
}

function html() {
    return gulp.src('./**/*.html')
        .pipe(gulp.dest('./build/'))
}

gulp.task('js', js);
gulp.task('css', css);
gulp.task('html', html);

// Browser-sync task, only cares about compiled CSS
gulp.task('browser-sync', function () {
    browserSync({
        server: {
            baseDir: "./build/"
        }
    });
});

gulp.task('build', function () {
    js();
    css();
    html();
});

// Dev task
gulp.task('default', gulp.series('css', 'js', 'html', 'browser-sync'), function () {
    gulp.watch('./frontend/css/*css', gulp.series('css', browserSync.reload));
    gulp.watch('./frontend/js/*.js', gulp.series('js', browserSync.reload));
    gulp.watch('./frontend/*.html', gulp.series('html', browserSync.reload));
});
